# AmberChargeTracker
Jetpack Compose Android App to record Electric car journeys and charging stats

It's been a while since I tried Android development.


## Task list

### Bugs 
- [ ] Really getting confused about ViewModels, init {} blocks, LaunchedEffects... I'm not getting the data I expect when I expect it
- [X] Ability to abort a charge event
- [X] Prevent editing of starting values on active charge event
- [X] Fix main menu still showing charge event after it's finished
- [X] Correctly handle startup if vehicles exist but SharedPreferences does not - or maybe **store selected vehicle etc in DB instead**?
- [X] Saving total cost of a charge event
  - There's no currency support, assumes UK£ only
- [X] Text styling on real device - black on black is hard to read...
- [X] Very inconsistent behaviour moving between screens when charging

### Technical improvements

- [X] Set up database migrations so that I can avoid wiping the DB
- [ ] field validation - really required for currency and percentages
  - _this did not go well when I tried to do it at the composable text input level, so probably needs to happen at the viewmodel - which would mean hoisting the fields into the VM?_
- [ ] cross-field validation at viewmodel level?
- [ ] Charging screen improvements
  - [X] Should persist across process death
  - [X] Check if charge is in progress on startup
  - [X] Restore the timer correctly based on the charge start time
  - [ ] Handle back navigation when charge event is saved (e.g from history screen)
- [X] Use `Scaffold` for layout

### Features

- [ ] Add better navigation - might come with `Scaffold`?
- [X] Enable switching between different vehicles - could be useful to keep dev separate from real, now that the app works in a basic way
- [ ] cross-field validation at ViewModel level?
- [ ] Charging screen improvements
  - [X] Should persist across process death
  - [X] Check if charge is in progress on startup
  - [X] Restore the timer correctly based on the charge start time
- [X] charge history table improvements
- [X] charge history graphs
- [ ] Journey recording screens and model
- [ ] Android Auto integration
- [X] Add a photograph of the cars for easy identification
- [X] Edit car details
- [X] Delete vehicle and all associated events, images etc
- [ ] A decent icon

