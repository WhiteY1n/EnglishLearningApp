# AGENTS.md

# English Learning Android Project Guidelines

This file defines the coding standards, architecture, and working rules for all AI coding agents (Antigravity, Cursor, Claude Code, etc.).

The highest priority is:

1. Keep the project stable.
2. Keep the project readable.
3. Never break existing features.
4. Only implement the requested feature.

---

# Project Overview

Project:

English Learning Android

Backend:

Laravel PHP REST API

Frontend:

Android
Kotlin
Jetpack Compose

Architecture:

MVVM
Repository Pattern

Networking:

Retrofit
OkHttp
Gson

State Management:

StateFlow

Dependency Injection:

Manual dependency injection.

DO NOT introduce Hilt, Koin or other DI frameworks unless explicitly requested.

---

# Main Features

Authentication

Flashcard Learning

Quiz Learning

Profile

Statistics

Practice

Future Features

Offline Cache

Dark Mode

Avatar Upload

Notifications

---

# Development Philosophy

Prefer simplicity over cleverness.

Write code that is easy for students to understand.

Avoid unnecessary abstractions.

Avoid overengineering.

Every feature should be easy to debug.

---

# General Rules

Only implement the feature requested by the prompt.

Do not refactor unrelated code.

Do not rename packages.

Do not reorganize folders.

Do not change architecture.

Do not change navigation unless requested.

Do not introduce new libraries without permission.

Project must compile after every task.

---

# Build Safety

Before finishing any task:

Gradle Sync

Build Project

Fix compilation errors

Never leave broken code.

Never leave TODO compile errors.

---

# Architecture

UI

↓

ViewModel

↓

Repository

↓

API

Never call Retrofit directly from Composables.

Never perform business logic inside UI.

Repositories are responsible for networking.

ViewModels are responsible for state.

Composable functions only display state.

---

# State Management

Always use

StateFlow

Do not introduce LiveData.

Prefer immutable UI state.

Create dedicated UI state classes.

Example:

Loading

Success

Empty

Error

---

# Navigation

Reuse existing Navigation.

Do not rewrite the navigation graph.

Do not create multiple NavHosts unless requested.

Keep routes centralized.

---

# UI Guidelines

Use Jetpack Compose.

Use Material 3.

Use existing Theme.

Keep UI consistent.

Avoid deeply nested Composables.

Split large screens into smaller components.

Maximum recommended:

200 lines per composable file.

---

# Code Style

Prefer descriptive names.

Bad:

data

temp

abc

Good:

flashcardCollection

quizRepository

profileState

Avoid magic numbers.

Avoid duplicated code.

Prefer extension functions when appropriate.

---

# Comments

Add comments only where logic is not obvious.

Do not comment every line.

Explain WHY.

Not WHAT.

---

# File Organization

One screen

↓

One ViewModel

↓

One Repository

↓

One API interface

↓

Models

Avoid huge files.

Maximum recommended:

300 lines.

---

# Networking

Use Retrofit.

Use OkHttp.

Use Gson.

Reuse existing Retrofit instance.

Reuse existing interceptors.

Authorization uses:

Bearer Token

Never hardcode tokens.

---

# API Models

Reuse existing models whenever possible.

Avoid duplicate DTOs.

Use

@SerializedName

for snake_case fields.

Handle nullable values safely.

---

# Error Handling

Handle:

Network Error

Unauthorized

Empty Response

Server Error

Unknown Error

Always show user-friendly messages.

Never crash.

---

# Loading States

Every screen that loads data should support:

Loading

Success

Error

Empty

Retry

---

# Forms

Validate before API calls.

Show validation errors.

Disable Save button while submitting.

Prevent duplicate requests.

---

# Images

Use Coil.

If avatar is null

show placeholder.

Do not implement image upload unless requested.

---

# Dates

Store backend date as String.

Format only in UI layer.

Never change backend format.

---

# Strings

Avoid hardcoded UI text.

Use string resources whenever practical.

---

# Logging

Use Log.d only during debugging.

Remove unnecessary logs before finishing.

Do not print tokens.

Do not print passwords.

---

# Security

Never expose access tokens.

Never expose refresh tokens.

Never log Authorization headers.

---

# Performance

Avoid unnecessary recomposition.

Remember expensive calculations.

LazyColumn for long lists.

Avoid nested scrolling.

---

# Feature Development Workflow

Each feature should be implemented independently.

Example:

Login

↓

Profile

↓

Flashcard CRUD

↓

Quiz CRUD

↓

Statistics

↓

Settings

Do not modify finished features.

---

# Existing Features

These are considered stable.

Authentication

Flashcards

Quiz Learning

Do not modify them unless explicitly requested.

---

# Git Workflow

One feature

↓

One commit

Commit examples:

feat(profile): add profile screen

feat(profile): implement edit profile

feat(flashcard): add flashcard CRUD

fix(login): fix token refresh

Do not combine unrelated changes.

---

# Response Format

After completing a task, always provide:

Summary

Created files

Modified files

Important notes

Known limitations

Future TODOs

---

# If Requirements Are Unclear

Do not guess.

Use existing project patterns.

Reuse existing architecture.

Choose the simplest implementation.

---

# Absolute Rules

Never break Login.

Never break Flashcards.

Never break Quiz.

Never replace architecture.

Never introduce unnecessary libraries.

Never rewrite working code.

Always keep the project buildable.

Always keep code beginner-friendly.

Always prioritize readability over cleverness.