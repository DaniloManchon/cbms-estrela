function imprimirCodigoBarrasExterno(nomeResponsavel, codigoBarras, barcodeSvgHtml) {
    var win = window.open('', '_blank');
    win.document.write('<!DOCTYPE html><html><head><title>Etiqueta</title><style>');
    win.document.write('@page { size: 9cm 7cm; margin: 2mm; }');
    win.document.write('* { box-sizing: border-box; margin: 0; padding: 0; }');
    win.document.write('body { width: 100%; height: 100%; display: flex; justify-content: center; align-items: center; font-family: sans-serif; }');
    win.document.write('.label { text-align: center; width: 100%; }');
    win.document.write('.projeto { font-size: 8pt; font-weight: bold; color: #555; }');
    win.document.write('.nome { font-size: 11pt; font-weight: bold; margin: 2mm 0; color: #000; word-break: break-word; }');
    win.document.write('.barcode-wrap { margin: 2mm 0; display: flex; justify-content: center; }');
    win.document.write('.barcode-wrap svg { height: 12mm; width: auto; max-width: 8cm; }');
    win.document.write('.codigo { font-family: monospace; font-size: 9pt; letter-spacing: 2px; font-weight: bold; color: #333; }');
    win.document.write('</style></head><body>');
    win.document.write('<div class="label">');
    win.document.write('<div class="projeto">ONG Estrela - CBMS</div>');
    win.document.write('<div class="nome">' + nomeResponsavel + '</div>');
    win.document.write('<div class="barcode-wrap">' + barcodeSvgHtml + '</div>');
    win.document.write('<div class="codigo">' + codigoBarras + '</div>');
    win.document.write('</div>');
    win.document.write('<script>window.onload=function(){window.print();window.close()}<\/script>');
    win.document.write('</body></html>');
    win.document.close();
}
