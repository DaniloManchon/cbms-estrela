let membroIndex = document.querySelectorAll('#membrosCorpo tr').length;

function addMembroRow() {
    const tbody = document.getElementById('membrosCorpo');
    const row = document.createElement('tr');
    row.innerHTML = `
        <td><input type="text" name="identificacaoFamiliar[${membroIndex}].nomeCompleto" class="form-control form-control-sm" required></td>
        <td><input type="text" name="identificacaoFamiliar[${membroIndex}].parentesco" class="form-control form-control-sm" required></td>
        <td>
            <div class="input-group input-group-sm">
                <input type="text" name="identificacaoFamiliar[${membroIndex}].dataNascimento" class="form-control date-mask" placeholder="DD/MM/AAAA" maxlength="10">
                <button class="btn btn-outline-secondary" type="button" onclick="this.nextElementSibling.showPicker()">
                    <i class="bi bi-calendar3"></i>
                </button>
                <input type="date" class="fam-dp" style="position: absolute; opacity: 0; width: 1px; height: 1px; pointer-events: none;">
            </div>
        </td>
        <td><input type="text" name="identificacaoFamiliar[${membroIndex}].ocupacaoEscola" class="form-control form-control-sm"></td>
        <td class="text-center">
            <button type="button" class="btn btn-outline-danger btn-sm" onclick="this.closest('tr').remove()">
                <i class="bi bi-trash"></i>
            </button>
        </td>
    `;
    tbody.appendChild(row);
    const dateInput = row.querySelector('.date-mask');
    if (dateInput) aplicarMascaraData(dateInput);
    membroIndex++;
}

document.getElementById('addMembro').addEventListener('click', addMembroRow);

document.addEventListener('change', function(e) {
    if (e.target.type === 'date') {
        const dateInput = e.target;
        const textInput = dateInput.closest('.input-group').querySelector('.date-mask');
        if (textInput && dateInput.value) {
            const [year, month, day] = dateInput.value.split('-');
            textInput.value = `${day}/${month}/${year}`;
            textInput.dispatchEvent(new Event('input'));
        }
    }
});

const selectFonteRenda = document.getElementById('selectFonteRenda');
const divOutraFonte = document.getElementById('divOutraFonte');

if (selectFonteRenda) {
    selectFonteRenda.addEventListener('change', function() {
        if (this.value === 'Outros') {
            divOutraFonte.classList.remove('d-none');
        } else {
            divOutraFonte.classList.add('d-none');
            divOutraFonte.querySelector('input').value = '';
        }
    });
    if (selectFonteRenda.value === 'Outros') divOutraFonte.classList.remove('d-none');
}

const cepInput = document.getElementById('cep');
const spinner = document.getElementById('cepSpinner');

if (cepInput) {
    cepInput.addEventListener('blur', function() {
        let cep = this.value.replace(/\D/g, '');
        if (cep.length === 8) {
            spinner.style.display = 'block';
            fetch(`https://viacep.com.br/ws/${cep}/json/`)
                .then(response => response.json())
                .then(data => {
                    spinner.style.display = 'none';
                    if (!data.erro) {
                        document.getElementById('logradouro').value = data.logradouro;
                        document.getElementById('bairro').value = data.bairro;
                        document.getElementById('localidade').value = data.localidade;
                        document.getElementById('uf').value = data.uf;
                    } else {
                        alert('CEP não encontrado.');
                    }
                })
                .catch(() => {
                    spinner.style.display = 'none';
                    alert('Erro ao consultar o serviço de CEP.');
                });
        }
    });
}

const video = document.getElementById('webcam-preview');
const photoPreview = document.getElementById('photo-preview');
const placeholder = document.getElementById('placeholder');
const canvas = document.getElementById('canvas-capture');
const inputFoto = document.getElementById('inputFoto');
const btnAbrir = document.getElementById('btn-abrir-camera');
const btnCapturar = document.getElementById('btn-capturar');
const btnResetar = document.getElementById('btn-resetar');
let stream = null;

btnAbrir.addEventListener('click', async () => {
    try {
        stream = await navigator.mediaDevices.getUserMedia({ video: true, audio: false });
        video.srcObject = stream;
        video.style.display = 'block';
        photoPreview.style.display = 'none';
        placeholder.style.display = 'none';
        btnAbrir.classList.add('d-none');
        btnCapturar.classList.remove('d-none');
    } catch (err) {
        console.error("Erro ao acessar a webcam: ", err);
        alert("Não foi possível acessar a webcam. Verifique as permissões.");
    }
});

btnCapturar.addEventListener('click', () => {
    canvas.width = video.videoWidth;
    canvas.height = video.videoHeight;
    canvas.getContext('2d').drawImage(video, 0, 0);

    const dataUrl = canvas.toDataURL('image/jpeg');
    inputFoto.value = dataUrl;
    photoPreview.src = dataUrl;

    photoPreview.style.display = 'block';
    video.style.display = 'none';
    btnCapturar.classList.add('d-none');
    btnResetar.classList.remove('d-none');

    if (stream) {
        stream.getTracks().forEach(track => track.stop());
    }
});

btnResetar.addEventListener('click', () => {
    inputFoto.value = '';
    photoPreview.style.display = 'none';
    placeholder.style.display = 'flex';
    btnResetar.classList.add('d-none');
    btnAbrir.classList.remove('d-none');
    atualizarStatusAbas();
});

function atualizarStatusAbas() {
    const tabs = [
        { tabId: 'dados-tab', paneId: 'dados' },
        { tabId: 'moradia-tab', paneId: 'moradia' },
        { tabId: 'renda-tab', paneId: 'renda' },
        { tabId: 'educacao-tab', paneId: 'educacao' },
        { tabId: 'familia-tab', paneId: 'familia' }
    ];

    tabs.forEach(t => {
        const pane = document.getElementById(t.paneId);
        const tabBtn = document.getElementById(t.tabId);
        if (!pane || !tabBtn) return;

        const fields = Array.from(pane.querySelectorAll('input, select, textarea'));

        const validFields = fields.filter(f => {
            if (!f.name) {
                return false;
            }
            if (f.type === 'hidden' || f.type === 'checkbox' || f.type === 'radio' || f.type === 'button' || f.type === 'submit') {
                return false;
            }
            if (f.closest('.d-none')) {
                return false;
            }
            return true;
        });

        let emptyRequiredCount = 0;
        let emptyOptionalCount = 0;

        validFields.forEach(f => {
            const val = f.value ? f.value.trim() : '';
            if (val === '') {
                if (f.hasAttribute('required')) {
                    emptyRequiredCount++;
                } else {
                    const id = f.id || '';
                    const name = f.name || '';
                    const placeholderText = f.placeholder || '';
                    if (id !== 'obs' && !placeholderText.includes('Opcional') && !name.includes('complemento')) {
                        emptyOptionalCount++;
                    }
                }
            }
        });

        if (t.paneId === 'familia') {
            const rows = pane.querySelectorAll('#membrosCorpo tr');
            if (rows.length === 0) {
                emptyOptionalCount = 1;
            }
        }

        const badgeSpan = tabBtn.querySelector('.tab-status-badge');
        if (badgeSpan) {
            if (emptyRequiredCount > 0) {
                badgeSpan.innerHTML = `<span class="d-inline-block rounded-circle bg-danger ms-1" style="width: 8px; height: 8px; vertical-align: middle;" title="${emptyRequiredCount} campo(s) obrigatório(s) pendente(s)"></span>`;
            } else if (emptyOptionalCount > 0) {
                badgeSpan.innerHTML = `<span class="d-inline-block rounded-circle bg-warning ms-1" style="width: 8px; height: 8px; vertical-align: middle;" title="${emptyOptionalCount} campo(s) opcional(ais) vazio(s)"></span>`;
            } else {
                badgeSpan.innerHTML = `<span class="d-inline-block rounded-circle bg-success ms-1" style="width: 8px; height: 8px; vertical-align: middle;" title="Todos os campos preenchidos"></span>`;
            }
        }
    });
}

const statusSelect = document.getElementById('statusSelect');
const motivoDiv = document.getElementById('motivoInativacaoDiv');

function toggleMotivoField() {
    if (statusSelect.value === 'false') {
        motivoDiv.classList.remove('d-none');
    } else {
        motivoDiv.classList.add('d-none');
    }
}

if (statusSelect) {
    statusSelect.addEventListener('change', toggleMotivoField);
    toggleMotivoField();
}

document.addEventListener('DOMContentLoaded', () => {
    atualizarStatusAbas();

    const formEl = document.getElementById('beneficiarioForm');
    if (formEl) {
        formEl.addEventListener('input', atualizarStatusAbas);
        formEl.addEventListener('change', atualizarStatusAbas);
        formEl.addEventListener('click', () => {
            setTimeout(atualizarStatusAbas, 100);
        });
    }
});
