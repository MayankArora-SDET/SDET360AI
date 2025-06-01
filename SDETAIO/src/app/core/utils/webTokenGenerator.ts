import * as jwt from 'jsonwebtoken';
export function getWebToken(url: string, username: string) {
  const payload = { url, username }
  const secretkey: string = 'gJKW<x+]j8k[RXw'
  const token = jwt.sign(payload, secretkey, {
    expiresIn: '1h'  // Token expiration (optional)
  });
  return token;

}