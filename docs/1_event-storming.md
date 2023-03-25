# Rozdział 1:  Event Storming

## 1.1 Wstęp

W celu zrozumienia procesów oraz zidentyfikowania potencjalnych mikroserwisów w tworzonej aplikacji
do zamawiania jedzenia z restauracji, przeprowadzono sesję Event Stormingu. Metoda ta umożliwia
wizualizację i modelowanie procesów biznesowych w ramach systemu, co jest kluczowe przy
wykorzystaniu architektury mikroserwisowej. W niniejszym rozdziale przedstawione zostaną wyniki
sesji Event Stormingu, która została przeprowadzona samodzielnie z wykorzystaniem narzędzia Miro.

## 1.2 Narzędzie Miro

Miro to interaktywna platforma do współpracy, umożliwiająca tworzenie tablic z notatkami, rysunkami
i diagramami. Wybór tego narzędzia był podyktowany jego prostotą użytkowania oraz możliwościami
wizualizacji modelu związanego z Event Stormingiem.

## 1.3 Identifikacja kontekstów granicznych (Bounded Context)

W wyniku sesji Event Stormingu zidentyfikowano pięć kontekstów granicznych (Bounded Context), które
będą kluczowe dla tworzonej aplikacji. Są to:

1. Restaurant - obejmuje zarządzanie restauracjami, menu i dostępnością produktów.
2. Order - odpowiada za proces zamówienia, takie jak składanie, modyfikowanie i anulowanie zamówień.
3. Delivery - koncentruje się na logistyce dostaw, monitorowaniu statusu przesyłki oraz komunikacji
   z dostawcą.
4. Payment - zarządza procesem płatności, obejmuje różne metody płatności oraz obsługę transakcji.
5. User - dotyczy zarządzania kontami użytkowników, uwierzytelniania oraz zarządzania uprawnieniami.
6. Invoice - odpowiada za generowanie faktur oraz zarządzanie historią transakcji.

## 1.4 Analiza procesów

Każdy z wyżej wymienionych kontekstów granicznych został poddany analizie procesów, które występują
w ramach danej domeny. W trakcie analizy zidentyfikowano następujące kluczowe zdarzenia:

1. **Restaurant:**
    - Dodanie nowej restauracji
    - Aktualizacja informacji o restauracji
    - Dodanie/aktualizacja menu
    - Zmiana dostępności produktu

2. **Order:**
    - Utworzenie nowego zamówienia
    - Modyfikacja zamówienia
    - Anulowanie zamówienia
    - Potwierdzenie zamówienia
    - Przyjęcie zamówienia przez restaurację

3. **Delivery:**
    - Przypisanie dostawcy do zamówienia
    - Aktualizacja statusu dostawy
    - Odbiór zamówienia przez dostawcę
    - Dostarczenie zamówienia do klienta

4. **Payment:**
    - Przyjęcie płatności
    - Anulowanie płatności
    - Zwrot płatności

[//]: # (TODO)
