/* Multi-page dashboard UI (no API links visible).
 * Auth is Basic Auth stored in sessionStorage. */

const AUTH_KEY = "gv_basic_auth";

// Prefer localStorage so auth persists across pages and tabs.
// (sessionStorage is per-tab; users often open modules in new tabs and "lose" auth.)
function storage() {
  try {
    if (typeof localStorage !== "undefined") return localStorage;
  } catch {}
  try {
    if (typeof sessionStorage !== "undefined") return sessionStorage;
  } catch {}
  return null;
}

const el = (id) => document.getElementById(id);
const PAGE = document.body?.dataset?.page || "dashboard";

const toastEl = el("toast");
let toastTimer = null;
function toast(msg) {
  if (!toastEl) return;
  toastEl.textContent = msg;
  toastEl.classList.add("is-on");
  clearTimeout(toastTimer);
  toastTimer = setTimeout(() => toastEl.classList.remove("is-on"), 2400);
}

function setAuth(username, password) {
  const token = btoa(`${username}:${password}`);
  const s = storage();
  if (!s) throw new Error("Stockage navigateur indisponible");
  s.setItem(AUTH_KEY, token);
}

function clearAuth() {
  const s = storage();
  if (!s) return;
  s.removeItem(AUTH_KEY);
}

function authHeader() {
  const s = storage();
  const token = s ? s.getItem(AUTH_KEY) : null;
  return token ? { Authorization: `Basic ${token}` } : {};
}

async function api(path, opts = {}) {
  const res = await fetch(path, {
    ...opts,
    headers: {
      "Content-Type": "application/json",
      ...authHeader(),
      ...(opts.headers || {}),
    },
  });

  const ct = res.headers.get("content-type") || "";
  const isJson = ct.includes("application/json");
  const body = isJson ? await res.json().catch(() => null) : await res.text().catch(() => "");

  if (!res.ok) {
    const msg =
      (body && body.message)
        ? body.message
        : (res.status === 401 ? "Identifiants incorrects." : `Erreur HTTP ${res.status}`);
    const err = new Error(msg);
    err.status = res.status;
    err.body = body;
    throw err;
  }

  return body;
}

function escapeHtml(s) {
  return String(s ?? "")
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;")
    .replaceAll("'", "&#039;");
}

function setPillUser(u) {
  const p = el("pillUser");
  if (!p) return;
  if (!u) {
    p.textContent = "Non authentifie";
    return;
  }
  p.textContent = `${u.nomUtilisateur} (${u.role})`;
}

function openModal(id) {
  const m = el(id);
  if (!m) return;
  m.setAttribute("aria-hidden", "false");
}

function closeModal(id) {
  const m = el(id);
  if (!m) return;
  m.setAttribute("aria-hidden", "true");
}

function kvItem(k, v) {
  return `<div class="kv__item"><div class="kv__k">${escapeHtml(k)}</div><div class="kv__v">${escapeHtml(v)}</div></div>`;
}

async function login(username, password) {
  setAuth(username, password);
  try {
    await api("/api/me");
  } catch (e) {
    clearAuth();
    throw e;
  }
}

async function requireAuth() {
  const s = storage();
  if (!s || !s.getItem(AUTH_KEY)) {
    openModal("loginModal");
    return null;
  }
  try {
    return await api("/api/me");
  } catch {
    clearAuth();
    openModal("loginModal");
    return null;
  }
}

function wireAuthUi() {
  el("btnLogout")?.addEventListener("click", () => {
    clearAuth();
    setPillUser(null);
    openModal("loginModal");
  });

  el("btnUseDemo")?.addEventListener("click", () => {
    if (el("lgUser")) el("lgUser").value = "enseignant1";
    if (el("lgPass")) el("lgPass").value = "pass123";
  });

  el("formLogin")?.addEventListener("submit", async (e) => {
    e.preventDefault();
    const fd = new FormData(e.target);
    const username = String(fd.get("username") || "").trim();
    const password = String(fd.get("password") || "");
    const errEl = el("loginError");
    if (errEl) errEl.textContent = "";

    try {
      await login(username, password);
      closeModal("loginModal");
      toast("Connexion reussie");
      await boot(); // refresh page data
    } catch (err) {
      if (errEl) errEl.textContent = err.message || "Connexion impossible";
    }
  });
}

function renderTable(tableId, rowsHtml) {
  const t = el(tableId);
  const body = t?.querySelector("tbody");
  if (!body) return;
  body.innerHTML = rowsHtml.join("");
}

function setSelectOptions(selectId, items, getValue, getLabel) {
  const s = el(selectId);
  if (!s) return;
  s.innerHTML = items
    .map((it) => `<option value="${escapeHtml(getValue(it))}">${escapeHtml(getLabel(it))}</option>`)
    .join("");
}

async function downloadEtudiantCsv(etudiantId) {
  // Fetch as blob so the browser downloads it with auth.
  const res = await fetch(`/api/etudiants/${etudiantId}/releve.csv`, { headers: { ...authHeader() } });
  if (!res.ok) {
    throw new Error(res.status === 401 ? "Non authentifie" : `Erreur HTTP ${res.status}`);
  }
  const blob = await res.blob();
  const url = URL.createObjectURL(blob);
  const a = document.createElement("a");
  a.href = url;
  a.download = `releve-etudiant-${etudiantId}.csv`;
  document.body.appendChild(a);
  a.click();
  a.remove();
  URL.revokeObjectURL(url);
}

async function loadDashboard(me) {
  const [etudiants, cours, notes] = await Promise.all([
    api("/api/etudiants"),
    api("/api/cours"),
    api("/api/notes"),
  ]);

  el("statEtudiants").textContent = String(etudiants.length);
  el("statCours").textContent = String(cours.length);
  el("statNotes").textContent = String(notes.length);

  const pillRole = el("pillRole");
  if (pillRole) pillRole.textContent = `Role: ${me.role}`;
}

async function loadEtudiants() {
  const etudiants = await api("/api/etudiants");
  el("statEtudiants").textContent = String(etudiants.length);

  renderTable(
    "tableEtudiants",
    etudiants.map(
      (e) =>
        `<tr data-id="${e.id}"><td class="mono">${e.id}</td><td>${escapeHtml(e.nom)}</td><td class="mono">${escapeHtml(e.matricule)}</td></tr>`
    )
  );

  el("btnCloseEtudiant")?.addEventListener("click", () => closeModal("etudiantModal"));

  el("tableEtudiants")?.addEventListener("click", async (ev) => {
    const tr = ev.target?.closest("tr[data-id]");
    if (!tr) return;
    const id = tr.getAttribute("data-id");
    if (!id) return;

    const err = el("etError");
    if (err) err.textContent = "";

    try {
      const [notes, moyenne] = await Promise.all([
        api("/api/notes"),
        api(`/api/etudiants/${id}/moyenne`),
      ]);

      const e = etudiants.find((x) => String(x.id) === String(id));
      const etNotes = notes.filter((n) => String(n.etudiant?.id) === String(id));

      el("etSubtitle").textContent = e ? `${e.nom} (${e.matricule})` : `Etudiant #${id}`;
      const kv = el("etKv");
      if (kv) {
        kv.innerHTML = [
          kvItem("Etudiant ID", id),
          kvItem("Moyenne", String(moyenne)),
        ].join("");
      }

      renderTable(
        "tableEtNotes",
        etNotes.map((n) => {
          return `<tr>
            <td class="mono">${n.id}</td>
            <td>${escapeHtml(n.cours?.nom)}</td>
            <td class="mono">${escapeHtml(n.cours?.code)}</td>
            <td><span class="tag tag--ok mono">${escapeHtml(n.valeur)}</span></td>
            <td>${escapeHtml(n.enseignant?.nomUtilisateur)}</td>
          </tr>`;
        })
      );

      el("btnEtCsv").onclick = async () => {
        try {
          await downloadEtudiantCsv(id);
        } catch (e2) {
          toast(e2.message || "Erreur");
        }
      };

      openModal("etudiantModal");
    } catch (e1) {
      if (err) err.textContent = e1.message || "Erreur";
      openModal("etudiantModal");
    }
  });

  el("formEtudiant")?.addEventListener("submit", async (ev) => {
    ev.preventDefault();
    const fd = new FormData(ev.target);
    try {
      await api("/api/etudiants", {
        method: "POST",
        body: JSON.stringify({
          nom: String(fd.get("nom") || ""),
          matricule: String(fd.get("matricule") || ""),
        }),
      });
      ev.target.reset();
      toast("Etudiant ajoute");
      await loadEtudiants();
    } catch (err) {
      toast(err.message);
    }
  });
}

async function loadCours() {
  const cours = await api("/api/cours");
  el("statCours").textContent = String(cours.length);

  renderTable(
    "tableCours",
    cours.map(
      (c) => `<tr data-id="${c.id}"><td class="mono">${c.id}</td><td>${escapeHtml(c.nom)}</td><td class="mono">${escapeHtml(c.code)}</td></tr>`
    )
  );

  el("btnCloseCours")?.addEventListener("click", () => closeModal("coursModal"));

  el("tableCours")?.addEventListener("click", async (ev) => {
    const tr = ev.target?.closest("tr[data-id]");
    if (!tr) return;
    const id = tr.getAttribute("data-id");
    if (!id) return;

    const err = el("crError");
    if (err) err.textContent = "";

    try {
      const notes = await api("/api/notes");
      const c = cours.find((x) => String(x.id) === String(id));
      const crNotes = notes.filter((n) => String(n.cours?.id) === String(id));
      const avg = crNotes.length ? (crNotes.reduce((s, n) => s + Number(n.valeur || 0), 0) / crNotes.length) : 0;

      el("crSubtitle").textContent = c ? `${c.nom} (${c.code})` : `Cours #${id}`;
      const kv = el("crKv");
      if (kv) {
        kv.innerHTML = [
          kvItem("Cours ID", id),
          kvItem("Moyenne", avg.toFixed(2)),
        ].join("");
      }

      renderTable(
        "tableCrNotes",
        crNotes.map((n) => {
          return `<tr>
            <td class="mono">${n.id}</td>
            <td>${escapeHtml(n.etudiant?.nom)}</td>
            <td class="mono">${escapeHtml(n.etudiant?.matricule)}</td>
            <td><span class="tag tag--ok mono">${escapeHtml(n.valeur)}</span></td>
            <td>${escapeHtml(n.enseignant?.nomUtilisateur)}</td>
          </tr>`;
        })
      );

      openModal("coursModal");
    } catch (e1) {
      if (err) err.textContent = e1.message || "Erreur";
      openModal("coursModal");
    }
  });

  el("formCours")?.addEventListener("submit", async (ev) => {
    ev.preventDefault();
    const fd = new FormData(ev.target);
    try {
      await api("/api/cours", {
        method: "POST",
        body: JSON.stringify({
          nom: String(fd.get("nom") || ""),
          code: String(fd.get("code") || ""),
        }),
      });
      ev.target.reset();
      toast("Cours ajoute");
      await loadCours();
    } catch (err) {
      toast(err.message);
    }
  });
}

function openEditModal(note) {
  el("editError").textContent = "";
  el("editNoteId").value = String(note.id);
  el("editValeur").value = String(note.valeur ?? "");
  el("editMotif").value = "";
  openModal("editModal");
  setTimeout(() => el("editValeur")?.focus(), 0);
}

// Legacy helper (kept for compatibility); we now display history in the modal table.
async function showHistory(noteId) {
  await api(`/api/notes/${noteId}/historique`);
}

async function loadNotes(me) {
  const [etudiants, cours, notes] = await Promise.all([
    api("/api/etudiants"),
    api("/api/cours"),
    api("/api/notes"),
  ]);

  el("statNotes").textContent = String(notes.length);
  setSelectOptions("nEtudiant", etudiants, (e) => e.id, (e) => `${e.nom} (${e.matricule})`);
  setSelectOptions("nCours", cours, (c) => c.id, (c) => `${c.nom} (${c.code})`);

  const isJury = me.role === "JURY";
  const hint = el("hintNote");
  if (hint) hint.textContent = isJury ? "Role JURY: modification + historique." : "Role ENSEIGNANT: ajout de notes.";

  renderTable(
    "tableNotes",
    notes.map((n) => {
      const actions = isJury
        ? `<button class="btn btn--ghost" data-action="edit" data-id="${n.id}" type="button">Modifier</button>
           <button class="btn btn--ghost" data-action="history" data-id="${n.id}" type="button">Historique</button>`
        : `<button class="btn btn--ghost" data-action="history" data-id="${n.id}" type="button">Historique</button>`;

      return `<tr>
        <td class="mono">${n.id}</td>
        <td>${escapeHtml(n.etudiant.nom)} <span class="tag">${escapeHtml(n.etudiant.matricule)}</span></td>
        <td>${escapeHtml(n.cours.nom)} <span class="tag tag--warn">${escapeHtml(n.cours.code)}</span></td>
        <td><span class="tag tag--ok mono">${escapeHtml(n.valeur)}</span></td>
        <td>${escapeHtml(n.enseignant.nomUtilisateur)}</td>
        <td class="t-right">${actions}</td>
      </tr>`;
    })
  );

  // Note detail modal
  el("btnCloseNote")?.addEventListener("click", () => closeModal("noteModal"));
  el("btnNoteEdit")?.addEventListener("click", () => {
    const id = el("btnNoteEdit")?.dataset?.noteId;
    if (!id) return;
    const note = notes.find((x) => String(x.id) === String(id));
    if (!note) return;
    if (!isJury) {
      toast("Acces refuse");
      return;
    }
    openEditModal(note);
  });
  el("btnNoteHistory")?.addEventListener("click", async () => {
    const id = el("btnNoteHistory")?.dataset?.noteId;
    if (!id) return;
    try {
      await renderHistoryIntoTable(id);
      toast("Historique charge");
    } catch (e) {
      el("noteError").textContent = e.message || "Erreur";
    }
  });

  // Add note (enseignant only; backend enforces it too)
  el("formNote")?.addEventListener("submit", async (ev) => {
    ev.preventDefault();
    const fd = new FormData(ev.target);
    try {
      await api("/api/notes", {
        method: "POST",
        body: JSON.stringify({
          etudiantId: Number(fd.get("etudiantId")),
          coursId: Number(fd.get("coursId")),
          valeur: Number(fd.get("valeur")),
        }),
      });
      if (el("nValeur")) el("nValeur").value = "";
      toast("Note ajoutee");
      await loadNotes(me);
    } catch (err) {
      toast(err.message);
    }
  });

  // Jury edit + history
  el("btnCloseEdit")?.addEventListener("click", () => closeModal("editModal"));

  el("formEditNote")?.addEventListener("submit", async (ev) => {
    ev.preventDefault();
    const fd = new FormData(ev.target);
    const noteId = String(fd.get("noteId") || "");
    const nouvelleValeur = Number(fd.get("nouvelleValeur"));
    const motif = String(fd.get("motif") || "").trim();
    el("editError").textContent = "";

    try {
      await api(`/api/notes/${noteId}`, {
        method: "PUT",
        body: JSON.stringify({ nouvelleValeur, motif }),
      });
      closeModal("editModal");
      toast("Note modifiee");
      await loadNotes(me);
    } catch (err) {
      el("editError").textContent = err.message || "Erreur";
    }
  });

  el("tableNotes")?.addEventListener("click", async (ev) => {
    const btn = ev.target?.closest("button[data-action]");
    if (!btn) return;
    const action = btn.getAttribute("data-action");
    const id = btn.getAttribute("data-id");
    if (!id) return;

    try {
      if (action === "edit") {
        if (!isJury) {
          toast("Acces refuse");
          return;
        }
        const note = notes.find((x) => String(x.id) === String(id));
        if (!note) return;
        openEditModal(note);
      }
      if (action === "history") {
        await openNoteDetail(id, { focusHistory: true });
      }
    } catch (err) {
      toast(err.message);
    }
  });

  // Click anywhere on a row (excluding buttons) to open the note detail modal
  el("tableNotes")?.addEventListener("click", async (ev) => {
    if (ev.target?.closest("button")) return;
    const tr = ev.target?.closest("tr");
    const idCell = tr?.querySelector("td");
    const noteId = idCell?.textContent?.trim();
    if (!noteId) return;
    await openNoteDetail(noteId, { focusHistory: false });
  });

  async function renderHistoryIntoTable(noteId) {
    const items = await api(`/api/notes/${noteId}/historique`);
    if (!items.length) {
      renderTable(
        "tableHistorique",
        [`<tr><td colspan="6" style="color: rgba(233,238,252,.72); padding: 14px;">Aucun historique pour cette note.</td></tr>`]
      );
      return;
    }
    renderTable(
      "tableHistorique",
      items.map((h) => {
        const date = h.dateModification ? String(h.dateModification).replace("T", " ") : "";
        return `<tr>
          <td class="mono">${h.id}</td>
          <td class="mono">${escapeHtml(h.ancienneNote)}</td>
          <td class="mono">${escapeHtml(h.nouvelleNote)}</td>
          <td>${escapeHtml(h.motif)}</td>
          <td>${escapeHtml(h.modifiePar?.nomUtilisateur)}</td>
          <td class="mono">${escapeHtml(date)}</td>
        </tr>`;
      })
    );
  }

  async function openNoteDetail(noteId, { focusHistory } = { focusHistory: false }) {
    el("noteError").textContent = "";
    const note = notes.find((x) => String(x.id) === String(noteId));
    if (!note) return;

    el("noteSubtitle").textContent = `${note.etudiant?.nom} (${note.etudiant?.matricule}) • ${note.cours?.code}`;
    const kv = el("noteKv");
    if (kv) {
      kv.innerHTML = [
        kvItem("Etudiant", `${note.etudiant?.nom} (${note.etudiant?.matricule})`),
        kvItem("Cours", `${note.cours?.nom} (${note.cours?.code})`),
        kvItem("Note", String(note.valeur)),
        kvItem("Enseignant", note.enseignant?.nomUtilisateur || "-"),
        kvItem("Note ID", String(note.id)),
        kvItem("Role", me.role),
      ].join("");
    }

    const btnEdit = el("btnNoteEdit");
    if (btnEdit) {
      btnEdit.dataset.noteId = String(noteId);
      btnEdit.style.display = isJury ? "inline-flex" : "none";
    }
    const btnHist = el("btnNoteHistory");
    if (btnHist) btnHist.dataset.noteId = String(noteId);

    await renderHistoryIntoTable(noteId);
    openModal("noteModal");

    if (focusHistory) {
      el("tableHistorique")?.scrollIntoView({ block: "start", behavior: "smooth" });
    }
  }
}

async function boot() {
  wireAuthUi();
  const me = await requireAuth();
  if (!me) return;
  setPillUser(me);

  if (PAGE === "dashboard") await loadDashboard(me);
  if (PAGE === "etudiants") await loadEtudiants();
  if (PAGE === "cours") await loadCours();
  if (PAGE === "notes") await loadNotes(me);
}

boot();
