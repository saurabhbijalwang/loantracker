@echo off
REM prepare_git.bat - initialize repo (if needed), untrack .idea/.github/target, commit .gitignore
cd /d %~dp0\..

echo ===CHECKING GIT REPOSITORY===
git rev-parse --is-inside-work-tree >nul 2>&1
if errorlevel 1 (
  echo Not a git repo. Initializing...
  git init
) else (
  echo Already a git repo.
)

echo ===SETTING GIT USER (LOCAL)===
git config user.name "auto-committer" >nul 2>&1
git config user.email "auto@local" >nul 2>&1

echo ===SHOW .gitignore===
if exist .gitignore (
  type .gitignore
) else (
  echo No .gitignore found
)

echo ===UNTRACK .idea .github target IF NEEDED===
ngit rm -r --cached --ignore-unmatch .idea 2>nul || echo .idea not tracked
ngit rm -r --cached --ignore-unmatch .github 2>nul || echo .github not tracked
ngit rm -r --cached --ignore-unmatch target 2>nul || echo target not tracked

echo ===ADDING ALL FILES===
git add -A

echo ===COMMITTING===
git commit -m "chore: initial commit (exclude .idea .github target)" || echo No changes to commit

echo ===STATUS===
git status --porcelain

echo ===CONFIRM .idea/.github/target ARE NOT TRACKED===
git ls-files .idea .github target || echo NONE_TRACKED

echo ===DONE===
echo If you want to add a remote and push, run these commands:
echo git remote add origin https://github.com/USERNAME/REPO.git
echo git branch -M main
echo git push -u origin main
pause

