# AmberChargeTracker
Jetpack Compose Android App to record Electric car journeys and charging stats

It's been a while since I tried Android development.


## Task list

- [ ] Saving total cost of a charge event
  - need to parse input string like "1.50" correctly, currently it fails and returns 0
- [ ] field validation - really required for currency and percentages
  - _this did not go well when I tried to do it at the composible text input level, so probably needs to happen at the viewmodel - which would mean hoisting the fields into the VM?_
- [ ] cross-field validation at viewmodel level?
- [ ] Charging screen improvements
  - [ ] Should persist across process death
  - [ ] Check if charge is in progress on startup
  - [ ] Restore the timer correctly based on the charge start time
- [ ] charge history table improvements
- [ ] charge history graphs
- [ ] Journey recording screens and model
- [ ] Android Auto integration

