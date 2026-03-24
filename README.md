<div align="center">

```
 ██████╗██████╗  ██████╗ ███╗   ██╗ ██████╗ ███████╗
██╔════╝██╔══██╗██╔═══██╗████╗  ██║██╔═══██╗██╔════╝
██║     ██████╔╝██║   ██║██╔██╗ ██║██║   ██║███████╗
██║     ██╔══██╗██║   ██║██║╚██╗██║██║   ██║╚════██║
╚██████╗██║  ██║╚██████╔╝██║ ╚████║╚██████╔╝███████║
 ╚═════╝╚═╝  ╚═╝ ╚═════╝ ╚═╝  ╚═══╝ ╚═════╝ ╚══════╝
```

**Мобильная платформа управления молодёжными мероприятиями**

[![Android](https://img.shields.io/badge/Android-API%2024%2B-3DDC84?style=flat-square&logo=android&logoColor=white)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9-7F52FF?style=flat-square&logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-2024.02-4285F4?style=flat-square&logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Supabase](https://img.shields.io/badge/Supabase-1.4.7-3ECF8E?style=flat-square&logo=supabase&logoColor=white)](https://supabase.com)
[![Hilt](https://img.shields.io/badge/Hilt-2.51-FF6F00?style=flat-square&logo=google&logoColor=white)](https://dagger.dev/hilt)
[![AI](https://img.shields.io/badge/AI-Qwen%203.5%20via%20GenAPI-FF6B35?style=flat-square)](https://gen-api.ru)

<br/>

> Система кадрового резерва · AI-аналитика · Реалтайм-мониторинг безопасности · 4 роли · 21 экран

<br/>

📄 **[Техническая документация](https://docs-cronos-two.vercel.app/)**

</div>

---

## Содержание

- [Обзор](#обзор)
- [Технологический стек](#технологический-стек)
- [Архитектура](#архитектура)
- [Роли пользователей](#роли-пользователей)
- [Экраны](#экраны)
- [AI-модуль](#ai-модуль)
- [Система античита](#система-античита)
- [Авторизация](#авторизация)
- [База данных](#база-данных)
- [PDF-экспорт](#pdf-экспорт)
- [Запуск](#запуск)

---

## Обзор

**CRONOS** — Android-приложение для управления молодёжными мероприятиями с системой кадрового резерва, AI-аналитикой на базе Qwen 3.5 и реалтайм-мониторингом безопасности.

Платформа объединяет четыре роли в едином интерфейсе:
- **Участники** соревнуются, растут по уровням и получают AI-рекомендации
- **Организаторы** создают мероприятия и регистрируют участников через QR
- **Наблюдатели** анализируют кадровый резерв и экспортируют AI-отчёты в PDF
- **Администраторы** контролируют безопасность платформы через систему античита

---

## Технологический стек

| Слой | Технология | Версия |
|------|-----------|--------|
| Language | Kotlin · JVM | 1.9 · JVM 17 |
| UI Framework | Jetpack Compose · Material 3 | BOM 2024.02.00 |
| Architecture | MVVM · Clean Architecture · StateFlow | — |
| DI | Dagger Hilt · KSP | 2.51 · 1.9.20-1.0.14 |
| Backend | Supabase (PostgreSQL · GoTrue · Realtime) | BOM 1.4.7 |
| HTTP Client | Ktor Client Android | 2.3.7 |
| Serialization | kotlinx.serialization | 1.6.2 |
| AI | Qwen 3.5 via GenAPI (OpenAI-compatible) | — |
| Charts | Vico compose-m3 | 1.13.1 |
| Camera | CameraX · ZXing | 1.3.1 · 3.5.2 |
| Images | Coil · coil-gif | 2.5.0 |
| PDF | Android PdfDocument · MediaStore API | — |
| Navigation | Navigation Compose | 2.7.6 |
| Build | Gradle KTS · AGP | 8.5 · 8.3.2 |

---

## Архитектура

Паттерн **MVVM + Clean Architecture**. Однонаправленный поток данных через `StateFlow`. Каждый экран имеет изолированный `UiState`.

```
com.cronos.app/
├── data/
│   ├── model/             # Profile · AnticheatUser · PortfolioItem · EventApplication
│   └── repository/        # ProfileRepository · AiRepository · AppStateRepository · EventRepository
├── di/
│   └── AppModule.kt       # @Singleton: SupabaseClient · HttpClient · репозитории
├── ui/
│   ├── components/        # CityDropdown — поиск по всем городам России
│   ├── navigation/        # CronosNavigation.kt — единый NavHost, 21 маршрут
│   ├── screens/
│   │   ├── auth/          # LoginScreen · LoginViewModel
│   │   ├── onboarding/    # Participant · Organizer · Observer onboarding
│   │   ├── dashboard/     # DashboardScreen — 4 роли в одном файле
│   │   ├── profile/       # ProfileScreen · ProfileViewModel
│   │   ├── events/        # EventsScreen · CreateEventScreen
│   │   ├── leaderboard/   # LeaderboardScreen
│   │   ├── messenger/     # MessengerScreen (ChatList + ChatDetail)
│   │   ├── stats/         # StatsScreen
│   │   ├── ai/            # AiHubScreen · AiHubViewModel
│   │   ├── inspector/     # InspectorScreen · InspectorViewModel
│   │   ├── organizer/     # OrganizerProfileScreen
│   │   ├── qr/            # QrScannerScreen
│   │   ├── rating/        # RatingScreen
│   │   └── admin/         # AdminProfileScreen · AdminMessengerScreen · AnticheatScreen+VM
│   └── theme/             # Color.kt · Theme.kt · Typography.kt
└── MainActivity.kt
```

### Поток данных

```
UI (Composable)
    ↕ collectAsState()
ViewModel (StateFlow<UiState>)
    ↕ suspend fun / Flow
Repository
    ↕
Supabase / Ktor / PdfDocument / CameraX
```

---

## Роли пользователей

```
┌─────────────────────────────────────────────────────────────────┐
│  participant  │  профиль · уровни · рейтинг · мероприятия       │
│               │  AI-резюме · план роста · мессенджер · портфолио │
├─────────────────────────────────────────────────────────────────┤
│  organizer    │  создание событий · QR-сканер · модерация заявок │
│               │  рейтинг доверия · управление призами            │
├─────────────────────────────────────────────────────────────────┤
│  observer     │  инспектор резерва · AI-скоринг · сравнение      │
│               │  PDF-отчёты · таймер обновления резерва (12ч)    │
├─────────────────────────────────────────────────────────────────┤
│  admin        │  античит · назначение модераторов · чат с ними   │
│               │  AI-сводка безопасности · PDF-отчёт платформы    │
└─────────────────────────────────────────────────────────────────┘
```

### Уровни участников

| Уровень | Баллы | Цвет |
|---------|-------|------|
| Bronze | 0 – 199 | `#CD7F32` |
| Silver | 200 – 499 | `#C0C0C0` |
| Gold | 500 – 999 | `#FFD700` |
| Reserve | 1000+ | `#00E5FF` |

---

## Экраны

| Экран | Маршрут | Роль |
|-------|---------|------|
| LoginScreen | `login` | Все |
| RoleSelectionScreen | `role_selection` | Новые |
| ParticipantOnboardingScreen | `participant_onboarding` | Участник |
| OrganizerOnboardingScreen | `organizer_onboarding` | Организатор |
| ObserverOnboardingScreen | `observer_onboarding` | Наблюдатель |
| DashboardScreen | `dashboard` | Все |
| ProfileScreen | `profile` | Участник |
| EventsScreen | `events` | Участник |
| LeaderboardScreen | `leaderboard` | Участник |
| MessengerScreen | `messenger` | Участник |
| StatsScreen | `stats` | Участник |
| AiHubScreen | `ai_hub` | Участник |
| RatingScreen | `rating` | Участник |
| InspectorScreen | `inspector` | Наблюдатель |
| CreateEventScreen | `create_event` | Организатор |
| QrScannerScreen | `qr_scanner` | Организатор |
| OrganizerProfileScreen | `organizer_profile` | Организатор |
| AdminProfileScreen | `admin_profile` | Админ |
| AdminMessengerScreen | `admin_messenger` | Админ |
| AnticheatScreen | `anticheat` | Админ |

---

## AI-модуль

Интеграция с **Qwen 3.5** через GenAPI (OpenAI-совместимый прокси).

```
endpoint    : https://proxy.gen-api.ru/v1/chat/completions
model       : qwen-3-5
max_tokens  : 1500
temperature : 0.7
```

> AI-текст **никогда не отображается в UI** — результат доступен только через PDF-экспорт.

| # | Функция | Экран | Описание |
|---|---------|-------|----------|
| 1 | Резюме достижений | AiHubScreen | Профессиональная самопрезентация 3–4 предложения |
| 2 | Подбор событий | AiHubScreen | Топ-3 мероприятия с обоснованием релевантности |
| 3 | План роста | AiHubScreen | Конкретный план достижения уровня Reserve |
| 4 | Скоринг кандидата | InspectorScreen | Оценка 1–10, риски, рекомендация |
| 5 | Сравнение кандидатов | InspectorScreen | 9 разделов, 400+ слов, многостраничный PDF |
| 6 | Сводка античита | AnticheatScreen | Паттерны аномалий, статистика, рекомендации |

### Парсинг ответа

`extractContent()` поддерживает 4 формата ответа API:

```kotlin
choices[0].message.content          // OpenAI string
choices[0].message.content[]        // OpenAI content-array
choices[0].message.reasoning_content // Qwen thinking mode
output / response                    // GenAPI native
```

Все ответы проходят через `stripMarkdown()` — удаление `**bold**`, `# headers`, `*italic*`.

---

## Система античита

Реалтайм-мониторинг активности с двумя режимами:

**AI-режим (по умолчанию)**
- Пользователи стримятся в список каждые `0.8с`, пауза каждые 10 записей
- LIVE-индикатор в топбаре (мигающий кружок)
- Статусы выставляются автоматически по алгоритму

**Ручной режим**
- Переключатель в топбаре останавливает стриминг
- Контекстное меню `⋮` на каждой карточке: Забанить / Предупреждение / Снять статус

```
BanStatus.BANNED      →  pointsPerDay > 80  |  duplicateIp  |  rapidLevelUp
BanStatus.SUSPICIOUS  →  eventsIn24h > 5
BanStatus.CLEAN       →  активность в норме
```

Распределение по 500 пользователям: ~8% забанено · ~14% подозрительных · ~78% чистых.

---

## Авторизация

```kotlin
// Supabase Auth — email + password через gotrue-kt
supabase.gotrue.signInWith(Email) {
    this.email = email
    this.password = password
}
// После входа: загрузка профиля → определение роли → дашборд

// Admin credentials: admin@gmail.com / admin123
```

Первый вход → `role_selection` → онбординг → `dashboard`  
Повторный вход → сразу `dashboard`

---

## База данных

Единственная таблица в Supabase — **`profiles`**.

```sql
CREATE TABLE profiles (
    id                  UUID PRIMARY KEY REFERENCES auth.users(id),
    display_name        TEXT NOT NULL,
    username            TEXT UNIQUE,
    city                TEXT,
    role                TEXT NOT NULL,          -- participant|organizer|observer|admin
    avatar_url          TEXT,
    birth_date          DATE,
    direction           TEXT,
    interests           TEXT[],
    verification_status TEXT DEFAULT 'pending', -- approved|pending|rejected
    rating              INTEGER DEFAULT 0,
    events_count        INTEGER DEFAULT 0,
    level               TEXT DEFAULT 'Bronze',  -- Bronze|Silver|Gold|Reserve
    created_at          TIMESTAMPTZ DEFAULT NOW()
);

-- Row Level Security
CREATE POLICY "own_profile" ON profiles
    USING (auth.uid() = id);
```

---

## PDF-экспорт

Все отчёты сохраняются в папку **Downloads** через `MediaStore API` (Android 10+).

| Отчёт | Файл | Источник |
|-------|------|----------|
| Скоринг кандидата | `Иван_Новиков.pdf` | InspectorScreen |
| Сравнение кандидатов | `Сравнение_Иванов_Петров.pdf` | InspectorScreen |
| AI-резюме / план роста | `<имя>.pdf` | AiHubScreen |
| Отчёт безопасности | `anticheat_report.pdf` | AnticheatScreen |

Многостраничный PDF с автоматическим переносом страниц при превышении высоты `800px`.

---

## Запуск

> Требуется **Android Studio Hedgehog** (2023.1.1) или новее · JDK 17

```bash
git clone <repo-url>
cd Cronos
```

Заполнить `local.properties` и `AppModule.kt` перед сборкой.

> ⚠️ Конфигурация Supabase и ключи API не включены в репозиторий.

---

## Документация

Полная техническая документация — **[docs-cronos-two.vercel.app](https://docs-cronos-two.vercel.app/)**

---

<div align="center">
<sub>CRONOS Platform · Android · Kotlin · Jetpack Compose · 2026</sub>
</div>
