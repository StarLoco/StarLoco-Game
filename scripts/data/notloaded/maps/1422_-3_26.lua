local map = MapDef(
	1422,
	"0706131721",
	"757960333D4456682C2F345C5326443C6A6C62762133617135226863327C597C5035433B414E243F577A317D7E5F54286A2532623F5C605129303859487B6957433A2D7E253235294329514532205E29432D472A685A4855562532626D7242606757297D28607D3532695D5C465A674C65493B4E5D4F5B75704669573B4F6D574E58625A51253235285F5D7D69416335656526695F5173643F6A",
	"HhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaej5iaaHhaaej5aaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaej5iaaGhaaeaaexKHhaaej5aaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaej5iaaHhaaeaaaZNHhaaeaaaaaHhaaej5aaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaej5iaaHhaaeaaaX8HhameaaaaaHhaaenQigwHhaaej5aaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaej5iaaHhaaebNaYcHhaaeaaaguHhaaem4aaaHhaaeaaaX8Hhaaej5aaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaej5iaaHhaaeaaaZQHhGmeaaaaaHhaaeaaahIHhGmeaaaaaHhaaeaaaaaHhaaej5aaaHhaaeaaaaaHhaaeaaaaaHhaaej5iaaGhaaenPixIHhGaeaaaaaHhaaeaaaYcHhaaeaaahIHhGaeaaaaaGhaaeaaaxTbhGaeaaaaaHhaaeaaaaaHhaaej5iaaHhGaeaaaaaHhGmeaaaaaHhGaeaaaaaHhameaaaguHhGaebNaaaHhameaaaZSbhGaeaaaaaHhaaeaaaaaHhaaej5iaaGhaaeaaavIHhGaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeHlaaaHhGaeaaaaabhGaeaaaaabhGaeaaaaaHhaaej5iaaHhaaeaaaZSHhGmeaaaaaHhaaeaaaaaHhGmeaaaaaHhGaeaaaaaHhGmeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaaHhaaeaaahZHhaaeaaaX8HhGaeaaaaaHhaaeZTaaaHhaaeaaaaaHhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaaHhaaeaaagCHhameaaaaaHhGaebNapDHhGmeaaaaaHhaaeaaaaaHhGmeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaaHhqaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeHlaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaaHhGmeaaaaaHhGaem4aaaHhGmeaaaaaHhJteqgaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaaHhaaeHlaYcGhaaeaaaxObhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaaHhameaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaa",
	10,
	14,
	-3,
	26,
	315
)

map.positions = "cacccjcmctcDcFcO|cKcUc3c6dbdkdldm"
map.capabilities = 32
map.mobGroupsCount = 3
map.mobGroupsSize = 8
map.allowedMobGrades = {
	{90, 1},
	{90, 2},
	{90, 3},
	{90, 4},
	{90, 5},
	{87, 1},
	{87, 2},
	{87, 3},
	{87, 4},
	{87, 5},
	{94, 1},
	{94, 2},
	{94, 3},
	{94, 4},
	{94, 5},
	{75, 1},
	{75, 2},
	{75, 3},
	{75, 4},
	{75, 5},
	{93, 1},
	{93, 2},
	{93, 3},
	{93, 4},
	{93, 5},
	{82, 1},
	{82, 2},
	{82, 3},
	{82, 4},
	{82, 5},
	{170, 1},
	{170, 2},
	{170, 3},
	{170, 4},
	{170, 5},
	{158, 1},
	{158, 2},
	{158, 3},
	{158, 4},
	{158, 5},
}
-- '0;0;0;0;0;0;0' forbiddens -> capabilities ? Or script ?

map.onMovementEnd = {
	[166] = moveEndTeleport(9813, 128),
	[205] = moveEndTeleport(1218, 245),
}

RegisterMapDef(map)
