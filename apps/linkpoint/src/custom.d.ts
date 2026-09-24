
/// <reference types="vite/client" />

interface ImportMetaEnv {
    readonly VITE_DEV_SERVER_URL?: string
}

interface ImportMeta {
    readonly env: ImportMetaEnv
}
