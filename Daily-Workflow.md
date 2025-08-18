# Daily Git/GitHub Workflow (Pahana-Edu)

Follow these steps every day to update your project and upload to GitHub.

## One-time setup
```bash
git clone https://github.com/MadushiWeerasinghe/Pahana-Edu.git
# Move your local folders pahana-edu-service/ and pahana-edu-web/ into the cloned folder if needed
cd Pahana-Edu
git add .
git commit -m "chore: initial structure"
git push origin main
git checkout -b develop
git push -u origin develop
```
> If you already cloned/committed, skip this.

## Daily routine (quick)
1. Make changes in `pahana-edu-service/` and/or `pahana-edu-web/`.
2. From the repo root, run:
   - **Windows PowerShell**: `.\daily-update.ps1 "feat: describe your change"`
   - **or** double-click `daily-update.bat` and type the message when prompted.
3. The script will:
   - Pull latest `develop` (or `main` if `develop` doesn't exist)
   - Create/switch to `feature/YYYY-MM-DD-daily-update`
   - Stage, commit, push to GitHub
   - Open your **Pull Request** page automatically
4. On GitHub, create the PR → wait for checks → **Merge**.
5. (Optional) Tag releases weekly:
```bash
git checkout main
git pull
git tag v0.1.0
git push origin v0.1.0
```

## Commit message tips (Conventional Commits)
- `feat: …` new feature
- `fix: …` bug fix
- `docs: …` documentation
- `refactor: …` code change w/o adding features
- `test: …` tests only
- `chore: …` tools/CI/config

## Troubleshooting
- **Nothing to commit**: make or save changes first.
- **Merge conflicts**: resolve in your editor, then:
```bash
git add -A
git rebase --continue   # or `git commit` if you merged
git push
```
- **Wrong branch**: `git checkout main` → `git branch -D feature/<name>` (if needed).
