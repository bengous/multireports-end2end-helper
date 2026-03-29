---
date: 2026-03-29
participants: Claude Opus 4.6 (orchestrateur) + GPT-5.4 via Codex CLI (analyse)
claude_session_id: ddd8c527-3637-4091-8ca7-33d15bf779d6
claude_model: claude-opus-4-6
codex_session_id: 019d3a34-b5b0-7492-9769-2a27e077f7c6
codex_model: gpt-5.4
codex_reasoning_effort: xhigh
codex_tokens_used: 76204
---

# Brainstorm -- multireports-end2end-helper

## Verdict global

Le framework a un bon instinct de base : garder le DSL de test petit, rendre les etapes de scenario visibles dans Allure, et garder les assertions HTTP proches du test. Mais le repo est encore plus un **prototype prometteur** qu'un framework scalable -- beaucoup de comportement repose sur de l'etat global et des conventions plutot que sur une architecture explicite.

---

## 1. Architecture -- Forces & Faiblesses

**Forces :** simplicite, tests lisibles, primitive de scenario utile (StepChain).

**Faiblesses (plus marquees) :**
- Tout le code framework vit sous `src/test/java` -- reutilisation et auto-tests compliques
- `BaseTest` concentre trop de responsabilites : resolution de config, bootstrap RestAssured, logging filesystem, attachements Allure, nommage des tests
- Globaux mutables (`BaseTest.API_URL`, `RestAssured.baseURI`) -- bloquant des qu'on veut tourner en parallele ou tester plusieurs APIs

## 2. StepChain -- Bug FIXME & Evolution

**Root cause du bug `runVoidStep` :** une lambda comme `ctx -> ctx.toBuilder().foo(...).build()` compile en `ThrowableContextRunnableVoid<T>`, mais le nouveau contexte est **silencieusement ignore** car `VoidContextStep.execute()` retourne toujours le contexte original.

**Proposition :** remplacer l'API par trois verbes explicites :
- `map(name, ThrowingUnaryOperator<C>)` -- transforme le contexte
- `tap(name, ThrowingConsumer<C>)` -- observe sans modifier
- `run(name, ThrowingRunnable)` -- sans contexte

Aussi : ne plus appeler `Assert.fail(...)` a la fin -- propager la cause originale pour que la stacktrace pointe vers l'etape qui a echoue, pas le framework.

## 3. StepChain -- Doit-il grandir ?

**Non, pas en workflow engine.** Le branchement conditionnel devrait rester en Java pur, les retries en helper explicite pour les steps idempotents, et les steps paralleles seulement s'il y a un vrai cas de fan-out read-only. Sequentiel, explicite, debuggable = bon tradeoff pour de l'E2E API.

## 4. BaseTest -- Trop de responsabilites

Decouper en : `EnvironmentResolver`, `RestAssuredBootstrap`, `TestLogLifecycle`. Aussi renommer les params TestNG (`apiURL`/`apiURLfallback`) car ce sont des **cles de proprietes**, pas des URLs.

## 5. RestAssuredClient -- Rester thin ou enrichir ?

En l'etat il n'ajoute quasiment rien au-dela de `given().get()`. Deux options :
- Le supprimer
- Le garder thin et construire des **clients par API** dessus : `PostsApi`, `UsersApi`, `AuthApi` -- chacun wrappant request specs, auth, et validation de reponse

> Ce qui scale bien n'est pas "une grosse abstraction HTTP custom" mais "un transport partage thin + des clients domaine".

## 6. Gestion des donnees de test

Les tests melangent orchestration, payloads, et assertions inline. Aller vers :
- Fixtures basees sur les resources : `requests/`, `expected/`, `cases/`
- Records de cas de test nommes avec TestNG `DataProvider`s
- `JsonAssertion.makeExpectedLookLikeActual` est utile mais devrait etre un **mode lenient opt-in**, pas le defaut pour des tests de contrat

## 7. Capacites manquantes de framework mature

- Config d'environnement typee
- Helpers auth/token
- Polling pour eventual consistency (Awaitility)
- Setup/cleanup de fixtures
- Isolation parallele-safe des tests
- Auto-tests du framework
- Request/response specs, correlation IDs, timeouts
- Stubs WireMock (le `WireMockManager` commente sugere que le besoin existe deja)
- Classification des echecs

## 8. Strategie de reporting

Le multi-report GitLab Pages est a mi-chemin :
- Le backup de trend est commente et marque broken dans `.gitlab-ci.yml`
- `allow_failure: true` preserve la generation de rapport mais peut masquer la sante de la suite
- Enrichir avec : metadata par run (API, env, branche, commit, duree, taux de succes, count flaky), vue de comparaison entre environnements
- Attention : stocker tous les rapports dans la branche `pages` finira par bloater

## 9. Opportunites Java 21

- **Sealed interfaces** pour les types de steps et modeles de cas de test
- Remplacer `SystemClock` custom par `Clock` ou `InstantSource` injecte
- **Structured concurrency / virtual threads** seulement pour des cas specifiques (comparaisons multi-env, probes smoke concurrentes) -- ne pas forcer dans le runner tant que le comportement Allure/logging/ThreadLocal n'est pas explicite

## 10. Sante des dependances

- **Bug :** `slf4j-simple` ET `log4j-slf4j-impl` sont presents -- multiple bindings SLF4J sur le classpath
- Dependencies probablement inutilisees dans ce snapshot : `jsonassert`, `mustache`, `jakarta.xml.bind-api`
- Beaucoup de deps devraient etre scope `test` de facon coherente puisque tout le framework est en test sources
- Ajouts utiles une fois les bases solides : `Awaitility`, WireMock ou Testcontainers, validation OpenAPI

## 11. Scalabilite a 10+ APIs

**Multi-module Maven :** `framework-core`, `framework-allure`, puis un module par API/domaine. Chaque API obtient ses propres clients, fixtures, suite config et tags. Le framework partage passe a `src/main/java`. CI devient une **matrice** sur API et environnement au lieu d'un `target.suite.xml.file` hardcode.

## 12. Experience developpeur

Le `README.md` est vide. Le gain DX le plus rapide : documentation + scaffolding -- template "nouvelle suite API", template "nouveau cas de test", layout de fixtures standard, et des helpers qui rendent le happy path evident.

---

## Recommandation strategique en 3 etapes

1. **Deplacer le framework reutilisable vers `src/main/java`** et decouper `BaseTest`
2. **Rendre StepChain plus petit mais plus sur** (map/tap/run, propager la cause)
3. **Construire les clients par API, fixtures, et matrixing CI** autour de ce coeur

> La chose a eviter c'est le sur-engineering : pas de workflow engine de test, pas de wrapping de chaque methode RestAssured, pas d'abstractions tant qu'au moins deux APIs n'ont pas besoin de la meme chose.
