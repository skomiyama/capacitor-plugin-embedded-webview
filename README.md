# @skomiyama/embedded-webview

capacitor plugin embedded webview

## Install

```bash
npm install @skomiyama/embedded-webview
npx cap sync
```

## API

<docgen-index>

* [`echo(...)`](#echo)
* [`create(...)`](#create)
* [`hide()`](#hide)
* [`show()`](#show)
* [`dismiss()`](#dismiss)
* [Interfaces](#interfaces)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### echo(...)

```typescript
echo(options: { value: string; }) => Promise<{ value: string; }>
```

| Param         | Type                            |
| ------------- | ------------------------------- |
| **`options`** | <code>{ value: string; }</code> |

**Returns:** <code>Promise&lt;{ value: string; }&gt;</code>

--------------------


### create(...)

```typescript
create(options: EmbeddedWebviewOptions) => Promise<void>
```

| Param         | Type                                                                      |
| ------------- | ------------------------------------------------------------------------- |
| **`options`** | <code><a href="#embeddedwebviewoptions">EmbeddedWebviewOptions</a></code> |

--------------------


### hide()

```typescript
hide() => Promise<EmbeddedWebviewVisibility>
```

**Returns:** <code>Promise&lt;<a href="#embeddedwebviewvisibility">EmbeddedWebviewVisibility</a>&gt;</code>

--------------------


### show()

```typescript
show() => Promise<EmbeddedWebviewVisibility>
```

**Returns:** <code>Promise&lt;<a href="#embeddedwebviewvisibility">EmbeddedWebviewVisibility</a>&gt;</code>

--------------------


### dismiss()

```typescript
dismiss() => Promise<void>
```

--------------------


### Interfaces


#### EmbeddedWebviewOptions

| Prop                | Type                                                                                  |
| ------------------- | ------------------------------------------------------------------------------------- |
| **`url`**           | <code>string</code>                                                                   |
| **`configuration`** | <code><a href="#embeddedwebviewconfiguration">EmbeddedWebviewConfiguration</a></code> |


#### EmbeddedWebviewConfiguration

| Prop               | Type                                                                                                                                     |
| ------------------ | ---------------------------------------------------------------------------------------------------------------------------------------- |
| **`styles`**       | <code>{ width: number; height: number; theme: <a href="#embeddedwebviewuicontrollertheme">EmbeddedWebViewUIControllerTheme</a>; }</code> |
| **`global`**       | <code>{ [key: string]: unknown; }</code>                                                                                                 |
| **`enableCookie`** | <code>boolean</code>                                                                                                                     |
| **`css`**          | <code>string</code>                                                                                                                      |


#### EmbeddedWebViewUIControllerTheme

| Prop         | Type                                                                                                                        |
| ------------ | --------------------------------------------------------------------------------------------------------------------------- |
| **`view`**   | <code>{ background: string; text: string; }</code>                                                                          |
| **`action`** | <code>{ cancel: { background: string; text: string; }; destructive: { text: string; }; default: { text: string; }; }</code> |


#### EmbeddedWebviewVisibility

| Prop             | Type                 |
| ---------------- | -------------------- |
| **`visibility`** | <code>boolean</code> |

</docgen-api>
