import i18n from "i18next";
import { initReactI18next } from "react-i18next";
import HttpBackend from "i18next-http-backend";

const BASE_URL = import.meta.env.VITE_BACKEND_URL;

i18n.use(HttpBackend)
    .use(initReactI18next)
    .init({
        lng: "en",
        fallbackLng: "en",
        ns: [],
        defaultNS: false,
        backend: {
            loadPath: `${BASE_URL}/api/i18n/{{ns}}`
        },
        react: {
            useSuspense: true
        }
    });

export default i18n;