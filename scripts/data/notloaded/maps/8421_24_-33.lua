local map = MapDef(
	8421,
	"0706131721",
	"63504A5347784E2753483F616D677174745A6E7A58215E3169632D7A586D286B616838286765632532623E546F3B673B4944284C7B6C3F4D634C7178434F74294125323523466B64572438213F724259543F35704A5574265870753C3760534B4131404C6A6A4F7D346C6D536F5E707468264D56582A6E2C31385E4B7870573D2E294A443542797E5A6137452F7B7D6A2366596E3D61503C34685C634678786A4B42573F4F73312F322D7277532742294D4B395636786E23394F763A3768756D72412A51315E513D7E7E4F4555293534",
	"bhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaaHhaaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaaHhaaeaaaaaHhaaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaabhGaeaaaaabhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeIvaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaabhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhekeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaabhGaeaaaaaHhaaeaaaaaHhaaeIqaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaIlHhaaeItaaaHhaaeaaaaabhGaeaaaaaHhaaeaaaaaHhaaeaaaIRHhmleaahY5GhaaeaaaIfHhKleaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaabhGaeaaaaaHhaaeIoaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeIuaaabhGaeaaaaabhGaeaaaaaGhekeaaaIcHhGaeaaaaaHhKkeaaaaaHhGaeIeaaaHhKkeaaaaaHhaaeJ6iaabhGaeaaaaabhGaeaaaaabhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhqaeqgaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaaHhKleaaaaaHhGaeaaaaaHhKleaaaaaHhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaaHhKkeaaaaaHhaaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaaGhaaeaaaIhbhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaa",
	9,
	11,
	24,
	-33,
	119
)

map.positions = "bJbSbXb0b5b6b8cc|bPbYb7cdceclcmcu"
map.mobGroupsCount = 3
map.mobGroupsSize = 3

-- '0;0;0;0;0;0;0' forbiddens -> capabilities ? Or script ?

map.onMovementEnd = {
	[125] = moveEndTeleport(8422, 155),
}

RegisterMapDef(map)