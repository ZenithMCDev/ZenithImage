
# ZenithImage
Manipulation d'image avec affichage dans Minecraft.

## Utilisation
Les paramètres des méthodes seront ici simplifiés pour la compréhension.

### Création et affichage d'une image :
```java
ModifiedImage image = ImageRender.get()
        .getImageManipulationHandler()
        .createModifiedImage(data...);

ImageRender.get().getImageManipulatorHandler().placeImage(image, data...);
```

### Création et affichage d'une image cliquable :

```java
ModifiedClickableImage image = ImageRender.get()
        .getImageManipulationHandler()
        .createModifiedClickableImage(data..., action);

ImageRender.get().getImageManipulatorHandler()
.placeImage(image, data...);
```

### Modification d'une image cliquable ou non :
```java

ModifiedImage image = ...
/* 
 Création d'un texte sur l'image à l'endroit (x, y) 
 avec la couleur (color) et la taille (size)
 */
image.drawText(String text, int x, int y, String fontName, Color color, int size);
```

D'autres méthodes, comme la création d'une ligne d'une coordonnée, sont disponibles dans l'objet `ModifiedImage`.

## Todo : 
- [ ] Documentation plus poussée
- [ ] Support vidéo
- [ ] Support GIF
- [ ] Optimisation de la méthode `update(data...)` dans l'objet `ModifiedImage`

## Auteur
- [Philouu](https://github.com/philougoatesque) 

Remerciements à :
- [Androzz](https://antique.gg/)
- [Lastril](https://github.com/Guillaume-BH)

## License
Ce projet est sous licence MIT - voir le fichier [LICENSE](LICENSE) pour plus de détails.

N'hésitez pas en cas de problème de faire une issue, les pull requests sont les bienvenues.
