# Rozdział 6: Wymagania niefunkcjonalne

W niniejszym rozdziale przedstawiono szczegółowe wymagania niefunkcjonalne aplikacji

## 6.1. Lista wymagań niefunkcjonalnych

- [ ] Skalowalność - każdy z komponentów systemu powinien być skalowalny w zależności od obciążenia. Np. każdy
  mikroserwis powinien móc być replikowany
- [ ] Wysoka dostępność - jeżeli jeden mikroserwis nie jest dostępny, inne mikroserwisy powinny nadal działać poprawnie
- [ ] Bezpieczeństwo - aplikacja powinna zapewniać odpowiednie zabezpieczenia w celu ochrony poufności, integralności i
  dostępności danych. Na przykład, dostęp do mikroserwisów powinien być chroniony przez autoryzację i uwierzytelnianie,
  a dane powinny być szyfrowane w tranzycie.
- [ ] Monitorowanie i logowanie - aplikacja powinna umożliwiać monitorowanie i logowanie zdarzeń w celu analizy i
  debugowania. Na przykład, każdy mikroserwis powinien logować swoje zdarzenia w centralnym repozytorium, a aplikacja
  powinna umożliwiać monitorowanie stanu mikroserwisów.
- [ ] Wydajność - system powinien umożliwiać na przetwarzanie minimum 10 żądań na sekundę
- [ ] Synchronizacja i spójność danych - system powinien zapewniać spójność danych na przełomie mikroserwisów.
- [ ] Testowalność - system powinien być łatwy do testowania, zarówno jednostkowo jak i integracyjnie
