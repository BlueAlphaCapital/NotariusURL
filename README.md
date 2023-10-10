# notarius-url-api
API created by Jeff Alcidor. Interview October 2023.

La premiere partie consiste avec le design de l'api!
*** Creation de l'url raccourci. On utilise:
-Endpoint: /shortenurl
-Method: POST

Il aura comme un input FULLURL, l'url original a raccourcir.
Comme sortie, SHORTURL, l'url crée.

*** Recuperation de l'url original.
-Endpoint: /agrandir/{url}
-Method: GET

Il aura comme un input SHORTURL, l'url a retourner a l'etat original.
Comme sortie, FULLURL, l'url original.


*** Recuperation une liste de toutes les URLS.
-Endpoint: /urls
-Method: GET
On va recuperer toutes les urls qui ont été créées.

Apres si on a le temps on peut essayer l'integration avec un front rapide.

