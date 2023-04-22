# Rozdział 3: Środowisko programistyczne

W tym rozdziale omówimy środowisko programistyczne używane do rozwoju i wdrożenia aplikacji. Środowisko programistyczne można podzielić na dwa rodzaje: lokalne i zdalne.

## 3.1 Środowisko lokalne

Środowisko lokalne składa się z następujących elementów:

- System operacyjny: MacOS 13.3.1
- Zintegrowane środowisko programistyczne (IDE): IntelliJ IDEA 2022.3.3
- Środowisko wdrożeniowe: Docker + Docker Compose

IDE IntelliJ IDEA jest używane do pisania, testowania i debugowania kodu. Docker i Docker Compose służą do tworzenia i zarządzania skonteneryzowanymi środowiskami dla aplikacji.

## 3.2 Środowisko zdalne

Środowisko zdalne jest używane do wdrożenia aplikacji do produkcji. Środowisko zdalne składa się z następujących elementów:

- System operacyjny: Ubuntu 22.04
- Specyfikacja maszyny: 4vCPU, 12GB RAM, 50GB SSD
- Lokalizacja: Sztokholm, Szwecja
- Klaster Kubernetes: MicroK8s 1.27

Środowisko zdalne jest hostowane na maszynie o wyższych specyfikacjach, aby zapewnić płynne działanie aplikacji w produkcji. Lokalizacja maszyny to Sztokholm, Szwecja, a działa ona na klastrze Kubernetes MicroK8s 1.27. Klastry Kubernetes są używane do zarządzania wdrożeniem aplikacji w sposób skalowalny i niezawodny.
