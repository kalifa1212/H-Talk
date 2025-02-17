
# H-Talk - API de Messagerie Instantanée

**H-Talk** est une API de messagerie instantanée développée par ***H technologies*** en **Java Spring Boot** permettant la communication en temps réel avec des amis et d'autres utilisateurs. Elle utilise **Keycloak** pour la gestion de la sécurité et de l'authentification, **PostgreSQL** comme base de données, et **WebSocket** pour une communication rapide et bidirectionnelle.

## Prérequis

Avant de commencer, assurez-vous que les outils suivants sont installés sur votre machine :

- **Java 17+** ou une version ultérieure.
- **Docker** et **Docker Compose** pour l'exécution des conteneurs.
- **Maven**  pour gérer les dépendances du projet.
- **IDE** tel que [IntelliJ IDEA](https://www.jetbrains.com/idea/) ou [Eclipse](https://www.eclipse.org/).

## Installation

### 1. Clonez le repository

```bash
git clone https://github.com/kalifa1212/H-Talk.git
cd H-Talk
```

### 2. Configuration de Docker

Le projet utilise **Docker Compose** pour orchestrer les services de l'API et de **Keycloak**. Assurez-vous d'avoir Docker et Docker Compose installés sur votre machine.

### 3. Lancer les services avec Docker Compose

Dans le répertoire du projet, exécutez la commande suivante pour démarrer les services définis dans `docker-compose.yml` :

```bash
docker-compose up --build
```

Cette commande va :

- Lancer un conteneur pour **Keycloak** (serveur d'authentification).
- Lancer un conteneur pour **Spring Boot** (l'API H-Talk).
- Lancer une base de données **PostgreSQL** pour stocker les messages et utilisateurs.

Les services seront disponibles à l'adresse suivante :

- **Keycloak** : `http://localhost:9090`
- **Spring Boot API** : `http://localhost:8080`
- **PostgreSQL** : `localhost:5432` (configuré dans `application.properties`)

### 4. Configuration de Keycloak

Accédez à **Keycloak** (via `http://localhost:9090`), connectez-vous avec les informations par défaut (`admin/admin`) et créez un **Realm** pour votre application de messagerie.

Créez également un **Client** pour l'authentification via OAuth2 et configurez les **roles** et **utilisateurs** nécessaires pour votre application.

### 5. Configuration de la base de données PostgreSQL

Les informations de connexion à la base de données PostgreSQL sont déjà configurées dans le fichier `application.yml`. Vous pouvez les modifier si nécessaire.

## Utilisation de l'API

L'API permet de gérer les utilisateurs, d'envoyer et de recevoir des messages instantanés. Voici quelques endpoints principaux :

### **Authentification avec Keycloak**

- **POST** `/auth/login`
    - Permet de se connecter à l'API via Keycloak.
    - Utilise le **OAuth2** pour obtenir un token d'accès.

### **Gestion des utilisateurs**

- **POST** `/api/users`
    - Crée un nouvel utilisateur.
    - **Body** : `JSON`

- **GET** `/api/users/{id}`
    - Retourne un utilisateur spécifique par ID.

### **Messagerie en temps réel via WebSocket**

- **WS** `/ws/chat`
    - Permet l'envoi et la réception de messages en temps réel via WebSocket.
    - Connectez-vous au WebSocket pour recevoir des notifications et envoyer des messages instantanément.

### Exemple de réponse JSON pour les messages :

```json
{
  "id": 1,
  "sender": "John",
  "receiver": "Jane",
  "message": "Hello, how are you?",
  "timestamp": "2025-02-17T15:30:00Z"
}
```

### **Exemple de connexion via WebSocket :**

```javascript
const socket = new WebSocket('ws://localhost:8080/ws/chat');

socket.onopen = () => {
  console.log("Connected to WebSocket");
  socket.send(JSON.stringify({ message: "Hello!" }));
};

socket.onmessage = (event) => {
  const message = JSON.parse(event.data);
  console.log("New message received:", message);
};
```

## Dockerfile

Le projet contient un `Dockerfile` pour la création du conteneur de l'API Spring Boot. Il permet de construire l'image Docker de l'application :

```bash
docker build -t h-talk-api .
docker run -p 8080:8080 h-talk-api
```

## Tests

Merci de testé l'application.



## Contribuer

1. Fork le projet.
2. Créez une branche pour votre fonctionnalité.
3. Committez vos modifications .
4. Push la branche .
5. Ouvrez une **Pull Request**.

## Licence

Ce projet est sous licence [MIT](LICENSE.md).

## Auteurs

- [Kalifa1212 GitHub](https://github.com/kalifa1212/)
