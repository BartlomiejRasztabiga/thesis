# Rozdział 8: Formaty i techniki wymiany danych

W komunikacji pomiędzy warstwa kliencką a warstwą serwerową wykorzystywane jest format JSON. Jest to format tekstowy,
który jest niezależny od platformy i języka programowania. Jest to format lekki, czytelny dla człowieka i łatwy do
analizy przez komputer. Wszystkie dane przesyłane pomiędzy warstwami są w formacie JSON.

Komunikacja ta wykorzystuje protokół HTTP. Jest to protokół bezstanowy, który nie wymaga utrzymywania połączenia
pomiędzy klientem a serwerem.

Komunikacja między mikroserwisami warstwy serwerowej odbywa się przy pomocy kolejki komunikatów Axon Server.
