describe('google.com.au (network dependent)', () => {
  it('loads google.com.au', () => {
    cy.visit('https://www.google.com.au/');
    cy.title().should('match', /google/i);
  });
});

