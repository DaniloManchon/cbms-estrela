document.addEventListener("DOMContentLoaded", function() {
    const barcodeEl = document.getElementById('barcode');
    const cod = barcodeEl?.dataset.codigo || '';

    if (cod) {
        JsBarcode("#barcode", cod, {
            format: "CODE128",
            width: 2,
            height: 40,
            displayValue: false,
            margin: 5
        });
    }
});

function imprimirCodigoBarras() {
    const barcodeEl = document.getElementById('barcode');
    const parentDiv = barcodeEl?.closest('[data-nome-completo]');
    const nome = parentDiv?.dataset.nomeCompleto || '';
    const cod = barcodeEl?.dataset.codigo || '';
    const svg = barcodeEl?.outerHTML || '';

    imprimirCodigoBarrasExterno(nome, cod, svg);
}
