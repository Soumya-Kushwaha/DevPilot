# DevPilot

**Ask questions about any GitHub repository and get answers that point to the exact lines of code they came from.**

DevPilot is a RAG (retrieval-augmented generation) assistant for codebases. You connect your GitHub account, pick a repository, and DevPilot indexes it. After that you can ask things like "where do we validate the token?" or "how does the retry logic work?" and get an answer that cites the files and line ranges it used, so you can check it instead of just trusting it.

I built the backend in Java 21 and Spring Boot. I wanted a project where the interesting problems were backend ones: background jobs, security, streaming, and search that actually returns the right thing.

## What it does

- **Indexes a repo in the background.** It fetches files through the GitHub API, so you don't have to clone anything. You can watch the progress while it runs.
- **Answers with citations.** Every answer shows which files and line ranges it drew from.
- **Streams the response.** Tokens arrive as they're generated (server-sent events), so you aren't staring at a spinner.
- **Keeps repositories separate.** Search is filtered per repository, so answers about one project never pull in code from another.

## How it works

```
GitHub API --> chunk files --> embed chunks --> pgvector (HNSW)
                                                     |
question --> embed --> filtered similarity search ---+--> LLM --> streamed answer + citations
```

A few decisions worth explaining:

- **Chunks follow line boundaries.** Each chunk covers whole lines, with some overlap between neighbours. That's what makes a citation like `src/Foo.java:42-67` possible, and the overlap stops an answer from missing something that sits right on a chunk boundary.
- **pgvector with an HNSW index.** I wanted vectors next to the rest of my data in PostgreSQL instead of running a separate vector database. HNSW keeps similarity search fast as the index grows.
- **Indexing is asynchronous.** Big repos take a while, so the API starts the job and returns right away, and progress is tracked separately.
- **Search is scoped per repository,** both for answer quality and so one user's data can't leak into another's results.

## Security

I treated this like a real multi-user app, since it handles access to people's repositories:

- Sign-in uses **GitHub OAuth2**.
- GitHub access tokens are stored **encrypted with AES-256**, never in plain text.
- **Every endpoint checks ownership**, so you can only reach repositories that belong to you.

## Tech stack

Java 21, Spring Boot, Spring AI, Spring Security (OAuth2), Spring Data JPA, PostgreSQL with pgvector, OpenAI embeddings.

## What I'd do next

- Re-index only the files that changed instead of the whole repo
- Add tests around the chunking and citation logic
- Try a re-ranking step to improve answer quality on large repos
  
## Feedback
If you have feedback or ideas, open an issue or say hi.
