// Fabrique un faux JWT pour les tests (Angular ne vérifie pas la signature, il décode seulement)
export function fauxJwt(sub: string, roles: string[], expDansSecondes = 3600): string {
  const b64 = (o: object) => btoa(JSON.stringify(o)).replace(/=/g, '').replace(/\+/g, '-').replace(/\//g, '_');
  const exp = Math.floor(Date.now() / 1000) + expDansSecondes;
  return b64({ alg: 'HS256', typ: 'JWT' }) + '.' + b64({ sub, roles, exp }) + '.signature';
}
