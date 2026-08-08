# CardVault — сборка на телефоне

1. Установи Termux из F-Droid, если у тебя его нет.
2. Скачай архив и распакуй его.
3. Открой Termux и перейди в папку проекта.
4. Выполни:

```bash
chmod +x build-termux.sh
./build-termux.sh
```

После сборки APK будет здесь:

`app/build/outputs/apk/debug/app-debug.apk`

Если архив находится в Downloads:

```bash
termux-setup-storage
cd ~/storage/downloads
unzip CardVault-Termux.zip
cd CardVault-Termux
chmod +x build-termux.sh
./build-termux.sh
```

Для загрузки Gradle требуется интернет. Первый запуск может занять несколько минут.
