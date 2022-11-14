# AmberChargeTracker
Jetpack Compose Android App to record Electric car journeys and charging stats

It's been a while since I tried Android development.


## Task list

- [X] Correctly handle startup if vehicles exist but SharedPreferences does not - or maybe **store selected vehicle etc in DB instead**?
- [ ] Saving total cost of a charge event
  - need to parse input string like "1.50" correctly, currently it fails and returns 0
- [ ] field validation - really required for currency and percentages
  - _this did not go well when I tried to do it at the composable text input level, so probably needs to happen at the viewmodel - which would mean hoisting the fields into the VM?_
- [ ] cross-field validation at ViewModel level?
- [ ] Charging screen improvements
  - [] Should persist across process death
  - [X] Check if charge is in progress on startup
  - [ ] Restore the timer correctly based on the charge start time
- [ ] charge history table improvements
- [ ] charge history graphs
- [ ] Journey recording screens and model
- [ ] Android Auto integration

