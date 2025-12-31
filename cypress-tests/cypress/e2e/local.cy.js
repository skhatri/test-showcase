describe('local smoke (fast)', () => {
  it('loads a local data page', () => {
    cy.visit('about:blank');
    cy.document().then((doc) => {
      doc.open();
      doc.write('<html><head><title>local</title></head><body>ok</body></html>');
      doc.close();
    });
    cy.title().should('eq', 'local');
    cy.contains('ok');
  });
});

