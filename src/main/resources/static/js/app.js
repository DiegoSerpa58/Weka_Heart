/**
 * WekaAI – Classification Dashboard
 * Handles: file upload (drag & drop), algorithm selection,
 *          REST calls to /api/weka/*, and result rendering.
 */

/* ── State ─────────────────────────────────────────────────── */
const state = {
  file:      null,
  algorithm: null,
  results:   null,
};

/* ── Icons mapped to algorithm IDs ─────────────────────────── */
const ALGO_ICONS = {
  ZeroR:      '◉',
  OneR:       '◎',
  NaiveBayes: '◈',
};

/* ═══════════════════════════════════════════════════════════
   Init
   ═══════════════════════════════════════════════════════════ */
document.addEventListener('DOMContentLoaded', () => {
  initFileUpload();
  initEvalMethodToggle();
  loadAlgorithms();
});

/* ═══════════════════════════════════════════════════════════
   Load algorithm list from backend
   ═══════════════════════════════════════════════════════════ */
async function loadAlgorithms() {
  try {
    const resp = await fetch('/api/weka/algorithms');
    if (!resp.ok) throw new Error('HTTP ' + resp.status);
    const algorithms = await resp.json();
    renderAlgorithmGrid(algorithms);
    if (algorithms.length > 0) selectAlgorithm(algorithms[0].id);
  } catch (e) {
    showToast('Could not load algorithm list: ' + e.message, 'error');
    document.getElementById('algorithm-grid').innerHTML =
      '<p style="color:var(--secondary);font-size:12px">Failed to load algorithms.</p>';
  }
}

/* ── Render algorithm cards ──────────────────────────────── */
function renderAlgorithmGrid(algorithms) {
  const grid = document.getElementById('algorithm-grid');
  grid.innerHTML = algorithms
    .map(a => `
      <div class="algorithm-card" id="algo-card-${a.id}"
           role="button" tabindex="0"
           onclick="selectAlgorithm('${a.id}')"
           onkeydown="if(event.key==='Enter')selectAlgorithm('${a.id}')">
        <div class="algo-icon">${ALGO_ICONS[a.id] || '⬡'}</div>
        <div class="algo-name">${a.name}</div>
        <div class="algo-desc">${a.description}</div>
      </div>`)
    .join('');
}

function selectAlgorithm(id) {
  state.algorithm = id;
  document.querySelectorAll('.algorithm-card').forEach(card => {
    card.classList.toggle('selected', card.id === `algo-card-${id}`);
  });
}

/* ═══════════════════════════════════════════════════════════
   File upload  (drag & drop + click)
   ═══════════════════════════════════════════════════════════ */
function initFileUpload() {
  const dropZone  = document.getElementById('drop-zone');
  const fileInput = document.getElementById('file-input');

  // Prevent browser from opening files
  ['dragenter', 'dragover', 'dragleave', 'drop'].forEach(evt => {
    dropZone.addEventListener(evt, e => { e.preventDefault(); e.stopPropagation(); });
  });

  dropZone.addEventListener('dragenter', ()  => dropZone.classList.add('drag-over'));
  dropZone.addEventListener('dragleave', ()  => dropZone.classList.remove('drag-over'));

  dropZone.addEventListener('drop', e => {
    dropZone.classList.remove('drag-over');
    const file = e.dataTransfer.files[0];
    if (file) handleFile(file);
  });

  // Click anywhere on drop zone (but not the button itself – already has its own onclick)
  dropZone.addEventListener('click', e => {
    if (!e.target.closest('.btn')) fileInput.click();
  });

  fileInput.addEventListener('change', e => {
    if (e.target.files[0]) handleFile(e.target.files[0]);
  });
}

function handleFile(file) {
  const ext = file.name.split('.').pop().toLowerCase();
  if (!['arff', 'csv'].includes(ext)) {
    showToast('Invalid file type. Please upload a .arff or .csv file.', 'error');
    return;
  }
  state.file = file;
  showFileInfo(file);
}

function showFileInfo(file) {
  document.getElementById('drop-zone').style.display  = 'none';
  const info = document.getElementById('file-info');
  info.style.display = 'flex';
  document.getElementById('file-name').textContent = file.name;
  document.getElementById('file-size').textContent = formatFileSize(file.size);
  document.getElementById('stat-dataset').textContent =
    file.name.replace(/\.(arff|csv)$/i, '');
}

function clearFile() {
  state.file    = null;
  state.results = null;

  document.getElementById('drop-zone').style.display  = '';
  document.getElementById('file-info').style.display   = 'none';
  document.getElementById('file-input').value          = '';

  document.getElementById('stat-dataset').textContent      = 'No dataset';
  document.getElementById('stat-instances').textContent    = '—';
  document.getElementById('stat-attributes').textContent   = '—';
  document.getElementById('stat-accuracy').textContent     = '—';

  const badge = document.getElementById('stat-accuracy-badge');
  badge.textContent = '';
  badge.className   = 'stat-badge';

  document.getElementById('results-empty').style.display  = '';
  document.getElementById('results-panel').style.display  = 'none';
}

/* ═══════════════════════════════════════════════════════════
   Evaluation method toggle
   ═══════════════════════════════════════════════════════════ */
function initEvalMethodToggle() {
  document.querySelectorAll('input[name="eval-method"]').forEach(radio => {
    radio.addEventListener('change', () => {
      const cv = radio.value === 'crossvalidation';
      document.getElementById('cv-settings').style.display = cv ? '' : 'none';
      document.getElementById('ps-settings').style.display = cv ? 'none' : '';
    });
  });
}

/* ═══════════════════════════════════════════════════════════
   Run classification
   ═══════════════════════════════════════════════════════════ */
async function runClassification() {
  if (!state.file) {
    showToast('Please upload a dataset first.', 'error');
    return;
  }
  if (!state.algorithm) {
    showToast('Please select an algorithm.', 'error');
    return;
  }

  const evalMethod   = document.querySelector('input[name="eval-method"]:checked').value;
  const folds        = parseInt(document.getElementById('folds').value)        || 10;
  const trainPercent = parseFloat(document.getElementById('train-percent').value) || 66;
  const seed         = parseInt(document.getElementById('seed').value)         || 1;

  const formData = new FormData();
  formData.append('file',             state.file);
  formData.append('algorithm',        state.algorithm);
  formData.append('evaluationMethod', evalMethod);
  formData.append('folds',            folds);
  formData.append('trainPercent',     trainPercent);
  formData.append('seed',             seed);

  setLoading(true);

  try {
    const resp = await fetch('/api/weka/classify', { method: 'POST', body: formData });

    if (!resp.ok) {
      const err = await resp.json().catch(() => ({}));
      showToast(err.message || 'Classification failed (HTTP ' + resp.status + ').', 'error');
      return;
    }

    const results = await resp.json();
    state.results = results;
    renderResults(results);
    showToast('Classification complete!', 'success');
  } catch (e) {
    showToast('Network error: ' + e.message, 'error');
  } finally {
    setLoading(false);
  }
}

/* ═══════════════════════════════════════════════════════════
   Render results
   ═══════════════════════════════════════════════════════════ */
function renderResults(r) {
  // Update stat cards
  document.getElementById('stat-instances').textContent  = r.numInstances.toLocaleString();
  document.getElementById('stat-attributes').textContent = r.numAttributes;
  document.getElementById('stat-accuracy').textContent   = r.accuracy.toFixed(1) + '%';

  const badge = document.getElementById('stat-accuracy-badge');
  badge.textContent = '↑ ' + r.accuracy.toFixed(1) + '%';
  badge.className   = 'stat-badge visible';

  // Show panel
  document.getElementById('results-empty').style.display = 'none';
  document.getElementById('results-panel').style.display = '';

  // Algorithm badge
  document.getElementById('result-algorithm-badge').textContent = r.algorithm;

  // Accuracy ring + details
  updateAccuracyRing(r.accuracy);
  document.getElementById('detail-dataset').textContent   = r.datasetName || '—';
  document.getElementById('detail-algorithm').textContent = r.algorithm;
  document.getElementById('detail-evaluation').textContent = r.evaluationMethod;
  document.getElementById('detail-instances').textContent =
    r.numInstances.toLocaleString() + ' instances';
  document.getElementById('detail-kappa').textContent = r.kappa.toFixed(4);
  document.getElementById('detail-mae').textContent   = r.meanAbsoluteError.toFixed(4);

  // Confusion matrix
  renderConfusionMatrix(r.confusionMatrix, r.classNames);

  // Per-class metrics
  renderMetricsTable(r.precision, r.recall, r.fMeasure, r.classNames);
}

/* ── Accuracy ring (SVG) ─────────────────────────────────── */
function updateAccuracyRing(accuracy) {
  const circumference = 2 * Math.PI * 42; // r=42
  const offset = circumference - (accuracy / 100) * circumference;
  document.getElementById('ring-fill').style.strokeDashoffset = offset;
  document.getElementById('accuracy-value').textContent = accuracy.toFixed(1) + '%';
}

/* ── Confusion matrix ────────────────────────────────────── */
function renderConfusionMatrix(matrix, classNames) {
  const container = document.getElementById('confusion-matrix-container');

  // Find max value for color intensity scaling
  let maxVal = 0;
  for (const row of matrix) {
    for (const v of row) { if (v > maxVal) maxVal = v; }
  }

  let html = '<table class="confusion-matrix"><thead><tr>';
  html += '<th class="cm-corner">Actual \\ Predicted</th>';
  for (const cls of classNames) {
    html += `<th class="cm-header">${cls}</th>`;
  }
  html += '</tr></thead><tbody>';

  for (let i = 0; i < matrix.length; i++) {
    html += `<tr><th class="cm-header">${classNames[i]}</th>`;
    for (let j = 0; j < matrix[i].length; j++) {
      const val    = Math.round(matrix[i][j]);
      const isDiag = (i === j);
      const t      = maxVal > 0 ? val / maxVal : 0;       // 0–1 intensity

      let bg = 'transparent';
      if (isDiag && val > 0) {
        bg = `rgba(74,222,128,${(0.15 + t * 0.55).toFixed(2)})`;
      } else if (!isDiag && val > 0) {
        bg = `rgba(252,165,165,${(0.2 + t * 0.6).toFixed(2)})`;
      }

      html += `<td class="cm-cell${isDiag ? ' cm-diagonal' : ''}" style="background:${bg}">${val}</td>`;
    }
    html += '</tr>';
  }

  html += '</tbody></table>';
  container.innerHTML = html;
}

/* ── Per-class metrics table ─────────────────────────────── */
function renderMetricsTable(precision, recall, fMeasure, classNames) {
  const container = document.getElementById('metrics-table-container');

  let html =
    '<table class="metrics-table"><thead><tr>' +
    '<th>Class</th><th>Precision</th><th>Recall</th><th>F1 Score</th>' +
    '</tr></thead><tbody>';

  for (let i = 0; i < classNames.length; i++) {
    const p = precision[i] || 0;
    const r = recall[i]    || 0;
    const f = fMeasure[i]  || 0;
    html += `<tr>
      <td class="class-name">${classNames[i]}</td>
      <td>${metricBar(p)}</td>
      <td>${metricBar(r)}</td>
      <td>${metricBar(f)}</td>
    </tr>`;
  }

  html += '</tbody></table>';
  container.innerHTML = html;
}

function metricBar(value) {
  const pct = Math.round(value * 100);
  return `<div class="metric-bar-container">
    <div class="metric-bar-bg">
      <div class="metric-bar" style="width:${pct}%"></div>
    </div>
    <span class="metric-value">${pct}%</span>
  </div>`;
}

/* ═══════════════════════════════════════════════════════════
   Utilities
   ═══════════════════════════════════════════════════════════ */
function formatFileSize(bytes) {
  if (bytes < 1024)        return bytes + ' B';
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB';
  return (bytes / (1024 * 1024)).toFixed(1) + ' MB';
}

function setLoading(on) {
  document.getElementById('loading-overlay').style.display = on ? 'flex' : 'none';
  document.getElementById('run-btn').disabled = on;
}

function showToast(message, type = 'error') {
  const toast = document.createElement('div');
  toast.className = `toast toast-${type}`;
  toast.textContent = message;
  document.body.appendChild(toast);
  setTimeout(() => toast.remove(), 4500);
}
