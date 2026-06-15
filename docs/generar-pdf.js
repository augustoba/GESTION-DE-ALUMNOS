/**
 * Genera FLUJOS.pdf a partir de flujos.html usando Puppeteer.
 *
 * Uso:
 *   npm install puppeteer
 *   node docs/generar-pdf.js
 *
 * El PDF queda en: docs/FLUJOS.pdf
 */

const puppeteer = require('puppeteer');
const path      = require('path');
const fs        = require('fs');

(async () => {
  const htmlPath = path.resolve(__dirname, 'flujos.html');
  const pdfPath  = path.resolve(__dirname, 'FLUJOS.pdf');

  if (!fs.existsSync(htmlPath)) {
    console.error('❌  No se encontró flujos.html en', __dirname);
    process.exit(1);
  }

  console.log('🚀  Lanzando Chromium...');
  const browser = await puppeteer.launch({
    headless: 'new',
    args: ['--no-sandbox', '--disable-setuid-sandbox'],
  });

  const page = await browser.newPage();
  await page.goto(`file://${htmlPath}`, { waitUntil: 'networkidle0' });

  console.log('📄  Generando PDF...');
  await page.pdf({
    path: pdfPath,
    format: 'A4',
    printBackground: true,
    margin: { top: '15mm', bottom: '15mm', left: '20mm', right: '20mm' },
    displayHeaderFooter: true,
    headerTemplate: '<div></div>',
    footerTemplate: `
      <div style="width:100%;font-size:8pt;color:#999;font-family:Arial;
                  display:flex;justify-content:space-between;padding:0 20mm;">
        <span>IES Alfredo Coviello — Sistema de Gestión de Alumnos</span>
        <span>Página <span class="pageNumber"></span> de <span class="totalPages"></span></span>
      </div>`,
  });

  await browser.close();

  const size = (fs.statSync(pdfPath).size / 1024).toFixed(1);
  console.log(`✅  PDF generado: ${pdfPath}  (${size} KB)`);
})();
