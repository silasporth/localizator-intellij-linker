# Localizator Linker for IntelliJ Platform

This plugin makes it possible to open react-i18n usages in the localizator app.
The app can be found [here](https://github.com/Dustlayer/localizator).

```tsx
const { t } = useTranslation(undefined, { keyPrefix: "mainMenu.detailsTab" });
```

Usage like this:

```tsx
{
  t("description");
}
```

Becomes a link that can be opened which is then handled by the localizator app.

### Features

*   **Deep Linking to Localizator**: `Ctrl + Click` (or `Cmd + Click`) on a translation key to open it directly in the Localizator app.
*   **Documentation Tooltips**: Hover over a translation key to see the full key path (including prefixes) and a quick action link.
*   **Context Awareness**: Automatically handles `keyPrefix` defined in the `useTranslation` hook.


### How it Works

The plugin identifies translation keys within JavaScript/TypeScript files that are used with the `t` function. 

#### Supported Pattern
It specifically looks for the `useTranslation` hook pattern:

```tsx
const { t } = useTranslation('common', { keyPrefix: 'settings.profile' });

// The plugin identifies 'username' and links it as 'settings.profile.username'
return <div>{t('username')}</div>;
```

#### Integration
When a key is clicked, the plugin triggers a custom URI:
`localizator://open?file=<path>&key=<key>&prefix=<prefix>`

The Localizator app must be installed and registered to handle the `localizator://` protocol for navigation to work.

### Development

1.  Clone the repository.
2.  Open the project in IntelliJ IDEA.
3.  Use:
    ```bash
    ./gradlew runIde
    ```
