-- We just need this quest to be defined. It is NOT meant to be used for gameplay
local q1054 = Quest(1054, {})
q1054.isRepeatable = true

-- Make sure we cannot start this quest
q1054.startFor = function(_,_) end
