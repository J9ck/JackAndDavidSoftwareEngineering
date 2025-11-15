# To-Do List Feature - User Guide

## Overview
The License Manager now includes an integrated To-Do List for tracking project tasks and features.

## Accessing the To-Do List
1. Open the License Manager application
2. In the menu bar, click **Tools**
3. Select **To-Do List**
4. A dialog window will open showing all project tasks

## Default Tasks (Pre-populated)
When you first open the To-Do List, you'll see these 5 tasks:

| Status | Task | Priority | Notes |
|--------|------|----------|-------|
| ☐ | Security concerns | High | Review and address security vulnerabilities |
| ☐ | Task manager checking | High | Implement task manager integration |
| ☐ | Make sure that programs that remain open get accounted for | High | Track active program usage |
| ☑ | Actual Database | Medium | SQLite database implemented ✓ |
| ☑ | Spell checker (zom+ would be understood user meant zoom+) | Medium | Fuzzy matching implemented ✓ |

## Features

### 1. View Tasks
- Tasks are sorted by priority (High → Medium → Low)
- Checkbox shows completion status
- Color-coded table with alternating rows

### 2. Add New Task
1. Click the **Add Task** button (green)
2. Fill in the form:
   - **Task**: Description of what needs to be done
   - **Priority**: High, Medium, or Low
   - **Notes**: Additional details or context
3. Click **Save**

### 3. Complete/Uncomplete Tasks
- Click the checkbox in the first column to toggle completion
- Changes are saved automatically to the database

### 4. Delete Tasks
1. Select a task by clicking on its row
2. Click the **Delete Task** button (red)
3. Confirm the deletion

### 5. Refresh List
- Click the **Refresh** button (blue) to reload tasks from the database
- Useful if multiple users are accessing the same database

## Button Guide
- 🟢 **Add Task** - Opens form to create new task
- 🔴 **Delete Task** - Removes selected task
- 🔵 **Refresh** - Reloads tasks from database
- ⚫ **Close** - Closes the To-Do List window

## Priority Levels
- **High**: Critical tasks that should be addressed first (red/urgent)
- **Medium**: Important but not urgent tasks (yellow/moderate)
- **Low**: Nice-to-have features or minor improvements (green/optional)

## Database Storage
- All tasks are stored in the SQLite database
- Table name: `todos`
- Persists between application restarts
- Tasks are never lost unless explicitly deleted

## Example Workflow
1. Open To-Do List
2. Review current tasks
3. Mark completed tasks by checking the box
4. Add new tasks as requirements come in
5. Delete obsolete tasks
6. Track project progress over time

## Technical Details
- **Component**: `TodoManager.java`
- **Database Table**: `todos`
- **Columns**: id, task, priority, notes, completed, created_at
- **UI Framework**: Java Swing (JDialog)
- **Styling**: Matches main application theme

## Integration with Main Application
The To-Do List is seamlessly integrated:
- Same database connection as licenses
- Consistent UI styling and colors
- Accessible from Tools menu
- Modal dialog (focuses attention on tasks)
- Transaction safety (database operations)

## Benefits
1. **Project Management**: Track features and requirements
2. **Team Collaboration**: Shared task list for all users
3. **Progress Tracking**: Check off completed items
4. **Prioritization**: Sort by importance
5. **Documentation**: Notes field for context
6. **Persistence**: Never lose track of tasks
