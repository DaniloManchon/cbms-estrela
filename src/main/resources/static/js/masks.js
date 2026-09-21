function aplicarMascaraData(input) {
    input.addEventListener('input', function(e) {
        let v = e.target.value.replace(/\D/g, '').slice(0, 10);
        if (v.length >= 5) {
            v = v.replace(/^(\d{2})(\d{2})(\d{0,4}).*/, "$1/$2/$3");
        } else if (v.length >= 3) {
            v = v.replace(/^(\d{2})(\d{0,2}).*/, "$1/$2");
        }
        e.target.value = v;
    });
}

function aplicarMascaraCPF(input) {
    input.addEventListener('input', function(e) {
        let v = e.target.value.replace(/\D/g, '').slice(0, 11);
        if (v.length > 9) {
            v = v.replace(/^(\d{3})(\d{3})(\d{3})(\d{2}).*/, "$1.$2.$3-$4");
        } else if (v.length > 6) {
            v = v.replace(/^(\d{3})(\d{3})(\d{0,3}).*/, "$1.$2.$3");
        } else if (v.length > 3) {
            v = v.replace(/^(\d{3})(\d{0,3}).*/, "$1.$2");
        }
        e.target.value = v;
    });
}

function aplicarMascaraCelular(input) {
    input.addEventListener('input', function(e) {
        let v = e.target.value.replace(/\D/g, '').slice(0, 11);
        if (v.length > 10) {
            v = v.replace(/^(\d{2})(\d{5})(\d{4}).*/, "($1) $2-$3");
        } else if (v.length > 6) {
            v = v.replace(/^(\d{2})(\d{4})(\d{0,4}).*/, "($1) $2-$3");
        } else if (v.length > 2) {
            v = v.replace(/^(\d{2})(\d{0,5}).*/, "($1) $2");
        } else if (v.length > 0) {
            v = v.replace(/^(\d{0,2}).*/, "($1");
        }
        e.target.value = v;
    });
}

function aplicarMascaraCEP(input) {
    input.addEventListener('input', function(e) {
        let v = e.target.value.replace(/\D/g, '').slice(0, 8);
        if (v.length > 5) {
            v = v.replace(/^(\d{5})(\d{3}).*/, "$1-$2");
        }
        e.target.value = v;
    });
}

document.addEventListener('DOMContentLoaded', function() {
    document.querySelectorAll('.date-mask').forEach(aplicarMascaraData);
    document.querySelectorAll('.cpf-mask').forEach(aplicarMascaraCPF);
    document.querySelectorAll('.celular-mask').forEach(aplicarMascaraCelular);
    document.querySelectorAll('.cep-mask').forEach(aplicarMascaraCEP);
});
