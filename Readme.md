# ViewModel & LiveData : Comprendre le fonctionnement

## Introduction

Annoncée lors du Google I/O 2017 [(source)](https://www.youtube.com/watch?v=FrteWKKVyzI), l'Android Architecture Componenents fait partie des 4 catégories de la collection de bibliothèques de composants [Android Jetpack](https://developer.android.com/jetpack).

Cette architecture aide à gérer le cycle de vie des composants de l'interface utilisateur (activités/fragments), la communication entre l'interface utilisateur et la gestion de la persistance des données. Ce qui permet de développer des applications Android plus robustes, testables et maintenables.

![Exemple de l'Android Architecture Components](https://user.oc-static.com/upload/2018/03/13/15209311930352_final-architecture.png)

L'Android Architecture Components est composée des librairies suivantes : 
- Data Binding
- Lifecycles
- **LiveData**
- Navigation
- Paging
- Room
- **ViewModel**
- WorkManager

Dans ce tutoriel, nous allons nous intérresser aux librairies ViewModel et LiveData.

## Zoom sur les librairies ViewModel et LiveData

![Itération dans une application](https://miro.medium.com/max/1400/1*I9WPcnpGNuI4CjxxrkP0-g.png)

* Le **ViewModel** est une classe qui gère les données d'un composant d'interface utilisateur spécifique, tel qu'un fragment ou une activité, et contient la logique métier permettant de gérer la communication avec le reste de l'application (Exemple : appeler les classes de logiques métier).
Le ViewModel ne connaît pas les composants de l'interface utilisateur, il n'est donc pas affecté par les modifications de configuration, telles que la recréation d'une activité lors de la rotation de l'appareil.

* **LiveData** est un observeur qui est utilisé pour notifier la vue en cas de changement des données observées.
  
  Cette classe contient deux méthodes interressantes : `setValue(T value)` et `postValue(T value)`
  - `setValue` permet de mettre à jour et de notifier notre objet observé. C'est une méthode qui ne peut être utilisé que dans un thread principal. 
  - `postValue` publie une tâche dans un thread principal pour définir la valeur donnée.

![Observer](https://miro.medium.com/max/824/1*hjvCDY_2W4PpK7HQoHsS2Q.png)

## Comparatif du cycle de vie d'une activité et d'un ViewModel

Le ViewModel a pour but de récupérer et de conserver les informations nécessaires à une activité ou à un fragment. Il ne sera détruit qu’au moment où cette dernière sera finie et détruite : (exemple: une rotation de l'écran).

![](https://miro.medium.com/max/1400/1*86RjXnTJucJMkW4Xi4kUlA.png)

En vert, le cycle de vie d'un ViewModel. On peut constater qu'à chaque changement d'état du cycle de vie de l'activité, le ViewModel n'est pas impacté.


# Pratique : implementer un ViewModel et LiveData

Ci-dessous le résultat du projet que nous allons réaliser dans ce tutoriel.

[![ViewModelExample](https://img.youtube.com/vi/u6uEOEJE-QI/0.jpg)](http://www.youtube.com/watch?v=u6uEOEJE-QI)


Dans le projet nous avons 2 fragments : **ProfileFragment** et **BiographyFragment**
- `ProfileFragment` contient 2 champs texte permettant de renseigner ses noms et prénoms.
- `BiographyFragment` affiche les noms et prénoms renseignés dans `ProfileFragment` et donne la possibilité de mettre à jour la biographie de l'utilisateur en renseignant le champ biographie.
Le bouton "Mettre à jour" mette la biographie à jour dans les 2 fragments.

L'idée est de montrer que les deux fragments partagent la même source de données (l'objet profil).
Et que cette source reste disponible, même après rotation du téléphone.

## Étape 1 : Ajouter les dépendances au projet

Ajouter les dépendances ci-dessous dans le fichier `build.gradle` situé dans le dossier `app`

```gradle
// build.gradle (Module: app)

dependencies {
    def lifecycle_version = "2.2.0"

    // ViewModel
    implementation "androidx.lifecycle:lifecycle-viewmodel:$lifecycle_version"
    // LiveData
    implementation "androidx.lifecycle:lifecycle-livedata:$lifecycle_version"
}
```

## Étape 2 : Créer le modèle `Profile`

Ci-dessous la classe Profile :

```java
// Profile.java

public class Profile {
    private String firstName;
    private String lastName;
    private String biography;

    // GETTERS
    public String getFullName() {
        return String.format("%s %s", lastName, firstName);
    }
    ...

    // SETTERS
    ...
}
```

## Étape 3 : Créer la classe `ProfileViewModel`

```java
package com.willyfalone.viewmodelexample;

import androidx.lifecycle.ViewModel;

public class ProfileViewModel extends ViewModel {
    //...
}
```

Notre classe `ProfileViewModel` hérite de la classe ` androidx.lifecycle.ViewModel`.
Nous allons à la prochaine étape ajouter des méthodes et des attributs à cette classe pour la gestion des données du profil.

## Étape 4 : Implémenter l'objet `LiveData<Profile>`

```java
package com.willyfalone.viewmodelexample;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ProfileViewModel extends ViewModel {
    private MutableLiveData<Profile> profileLiveData;

    public LiveData<Profile> getProfileLiveData() {
        if(profileLiveData == null) {
            profileLiveData = new MutableLiveData<>();

            // Initialiser le profil
            profileLiveData.setValue(new Profile());
        }

        return profileLiveData;
    }

    public Profile getProfile() {
        return profileLiveData.getValue();
    }

    public void setProfile(Profile profile) {
        profileLiveData.setValue(profile);
    }
}
```

> Euh 🤯, c'est quoi un `MutableLiveData` ?

Un `MutableLiveData` [(docs)](https://developer.android.com/reference/androidx/lifecycle/MutableLiveData?hl=en) est une classe qui hérite de la classe `LiveData` et qui expose publiquement les méthodes `setValue(T value)` et `postValue(T value)` qui sont `protected` dans `LiveData`

> *Remarque : Les données stockées dans le ViewModel seront disponible jusqu'à la destruction de l'activité*.

## Étape 5 : Partager les données entre les fragments

Il est courant de partager des données entre les fragments d'une même activité.
Ces fragments peuvent partager le même ViewModel en utilisant la référence de l'activité à laquelle ils sont rattachés pour gérer cette communication.

Un fragment ou une activité conserve des références aux ViewModels sur lesquels il s'appuie à l'aide d'une instance de la classe ViewModelProvider.

```java
// ProfileFragment.java et BiographyFragment.java
//...
ProfileViewModel profileViewModel;

//...
@Override
public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    profileViewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);
}
```

> Regardons de plus près cette ligne : <br/>**profileViewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);**

`new ViewModelProvider(requireActivity())` crée un `ViewModelStore` puis lie `ViewModelStore` à la référence de l'activité qui lui est passée en paramètre (`requireActivity()`). Le `ViewModelStore` instancie à son tour notre classe ProfileViewModel grâce à la méthode `.get(ProfileViewModel.class)`. Lorque que cette méthode est appelée à nouveau, il renverra le ProfileViewModel préexistant associé à l'activité (`requireActivity()`).

> `requireActivity()` retourne la référence de l'activité rattachée à un fragment


* `ProfileFragment`

```java
public class ProfileFragment extends Fragment {

    //public void onCreate(Bundle savedInstanceState) {
    //...

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Souscrire à l'observeur LiveData<Profil>
        // Appeler la méthode `updateView` lorsque que notre modèle Profil est modifié
        profileViewModel.getProfileLiveData().observe(this, this::updateView);

        btnValidate.setOnClickListener(v -> updateProfileInfo());
    }

    public void updateView(Profile profile) {
        lastNameEditText.setText(profile.getLastName());
        firstNameEditText.setText(profile.getFirstName());
        biographyTextView.setText(profile.getBiography());
    }

    public void updateProfileInfo() {
        String lastName = lastNameEditText.getText().toString();
        String firstName = firstNameEditText.getText().toString();

        // Récupérer le profil
        Profile profile = profileViewModel.getProfile();
        profile.setLastName(lastName);
        profile.setFirstName(firstName);

        // Mettre à jour le profil
        profileViewModel.setProfile(profile);
    }
}
```

Cette ligne `profileViewModel.getProfileLiveData().observe(this, this::updateView)` permet de souscrire au LiveData. A chaque modification du modèle, la méthode `updateView` est appelée.

* `BiographyFragment`

```java
public class BiographyFragment extends Fragment {

    //public void onCreate(Bundle savedInstanceState) {
    //...

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Souscrire à l'observeur LiveData<Profil>
        // Appeler la méthode `updateView` lorsque que notre modèle Profil est modifié
        profileViewModel.getProfileLiveData().observe(this, this::updateView);

        btnUpdate.setOnClickListener(v -> updateProfileInfo());
    }

    public void updateView(Profile profile) {
        fullNameTextView.setText(profile.getFullName());
        biographyEditText.setText(profile.getBiography());
    }

    public void updateProfileInfo() {
        String biography = biographyEditText.getText().toString();

        // Récupérer le profil
        Profile profile = profileViewModel.getProfile();
        profile.setBiography(biography);

        // Mettre à jour le profil
        profileViewModel.setProfile(profile);
    }
}
```

## Ce que vous devez retenir

* Le ViewModel est conçue pour gérer et concerver les données liées à l'interface utilisateur indépendament du cycle de vie des composants (activité / fragment). C'est un excellent moyen de créer des applications Android robustes, testables et maintenables. 
  - Le ViewModel suivie durant tout le cycle de vie d'une activité / fragment.
  - Plusieurs UI components peuvent partager le même ViewModel.
  - Il ne faut jamais utiliser de contexte dans un ViewModel (pour respecter le patron de conception)
* Les LiveData permettent d'observer les modificiations des modèles au sein de notre ViewModel.

**Retrouvez le code source du projet sur GitHub : [Lien code source](https://github.com/willf80/viewmodel-livedata-example)**

**Version itérative du cours : [Genially](https://view.genial.ly/5efdce79edf12d0d65fb1b2d/presentation-viewmodel-et-livedata-comprendre-le-fonctionnement)**

## Sources

- https://developer.android.com/reference/androidx/lifecycle/ViewModel
- https://developer.android.com/jetpack/guide
- https://developer.android.com/reference/androidx/lifecycle/LiveData