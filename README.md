# t24_academy_library
- 研修時作業用のメインリポジトリ
- 具体的な開発環境構築および開発手順は、研修時に提供されたドキュメントを参照すること

# 使用言語
- Java \(ver.21\)
  - SpringBoot

# 端末に入れておくアプリ
- WSL(Ubuntu)
- Docker Desktop
- OpenJDK \(version 21~\)

# Dockerのコンテナ構成
- 各コンテナのDockerfileは、dockerディレクトリ配下に配置している

## Dockerコンテナ起動
- docker compose up -d
  - 起動状況はDocker Desktopや`docker compose logs`から確認可能
  - metateam_academy_appコンテナのみ、30～60秒待つ必要がある

## 起動するコンテナリスト
- metateam_academy_minio
  - アップロードした画像の保管用（[参考](https://min.io/)）
- metateam_academy_mysql
  - データベース（[MySQL](https://dev.mysql.com/doc/refman/8.0/ja/)）
  - 初期データはdocker/mysql/initdb内に配置しており、初回コンテナ起動時に自動的にcreate&insertされる。（要確認）
  - 以降はVolumeを消さない限りは保持される
- metateam_academy_app
  - アプリの本体が乗っている
  - ホットリロードのコマンドが自動で打たれるので、若干起動に時間がかかる
    - VSCode上でソースを保存するたびにコンテナも同期されるので、毎回少し待つ必要があることに注意

## 開発環境のURL
- Docker起動できたら、以下のURLでアクセスできる
  - http://localhost:8080/mt_library/login
  - http://localhost:9001/login

# IDE（統合開発環境）について
- 本アプリの開発環境構築は簡単に作業ができるように[VSCode](https://code.visualstudio.com/)の使用を前提にしている
  - 以下の拡張機能を導入する
    - Japanese Language Pack for Visual Studio Code
    - Debugger for Java
    - Extension Pack for Java
    - Language Support for Java(TM) by Red Hat
    - Test Runner for Java
    - Dev Containers
    - Docker
    - WSL
- VSCodeにてブレークポイントを置きデバッグしたい場合
  - Dockerコンテナが起動中
  - docker/mt_library配下にある`launch.json`を、.vscode配下にコピー
  - VSCodeの左側のタブから、"実行とデバッグ"を選択、`Debug (Attach) - Spring Boot`を選んで「デバッグの開始」ボタンを押下