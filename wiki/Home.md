# WinRescue -- Wiki

![WinRescue](../docs/assets/winrescue-logo-dark.png)

Bienvenue sur le wiki de **WinRescue**, l'application Android qui transforme votre smartphone roote en clavier USB HID pour executer des scripts de recuperation Windows.

---

## Sommaire

### Prise en main
- **[Demarrage rapide](Getting-Started.md)** -- Installez et lancez votre premier script en 5 minutes
- **[Configuration root](Root-Setup.md)** -- Guide complet pour rooter votre telephone (Magisk, KernelSU)

### Comprendre WinRescue
- **[Architecture technique](Architecture.md)** -- Couches logicielles, MVVM, injection de dependances
- **[Protocole USB HID](USB-HID-Protocol.md)** -- Comment WinRescue simule un clavier physique

### Utilisation
- **[Catalogue des scripts](Scripts-Catalog.md)** -- Les 22 scripts detailles par categorie
- **[Depannage](Troubleshooting.md)** -- FAQ et resolution des problemes courants

### Contribuer
- **[Guide de contribution](Contributing.md)** -- Conventions, workflow Git, creation de scripts

---

## Liens rapides

| Ressource | Lien |
|-----------|------|
| Code source | [GitHub](https://github.com/ronylicha/winrescue) |
| README | [README.md](../README.md) |
| Changelog | [CHANGELOG.md](../CHANGELOG.md) |
| Licence | [MIT](../LICENSE) |
| Architecture detaillee | [docs/concepts/architecture.md](../docs/concepts/architecture.md) |

---

## A propos

WinRescue est ne d'un constat simple : quand un PC Windows ne demarre plus, les solutions classiques (cle USB bootable, technicien sur site) demandent du materiel dedie et des connaissances avancees. Avec WinRescue, le smartphone que vous portez deja dans votre poche devient un outil de recuperation universel.

L'application est concue pour trois profils :

1. **Techniciens IT en entreprise** -- Intervention rapide sur un parc de postes Windows sans transporter de kit USB
2. **Power users** -- L'ami qui depanne la famille et les proches, sans avoir ses outils sous la main
3. **Administrateurs systeme / MSP** -- Couverture complete des scenarios de recuperation avec un seul outil

### Comment ca fonctionne

Le telephone, connecte par cable USB-C/OTG au PC cible, est reconnu comme un clavier USB standard. WinRescue envoie des rapports HID de 8 octets sur `/dev/hidg0` pour simuler des frappes clavier -- le PC ne fait aucune distinction avec un vrai clavier. Un wizard interactif guide l'utilisateur etape par etape, avec confirmation visuelle et possibilite de retry a chaque point.
