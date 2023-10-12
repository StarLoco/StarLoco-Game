local map = MapDef(
	1422,
	"0706131721",
	"757960333d4456682c2f345c5326443c6a6c62762133617135226863327c597c5035433b414e243f577a317d7e5f54286a2532423f5c605129303859487b6957433a2d7e253235294329514532205e29432d472a685a4855562532426d7242606757297d28607d3532695d5c465a674c65493b4e5d4f5b75704669573b4f6d574e58625a51253235285f5d7d69416335656526695f5173643f6a",
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
map.mobGroupsMaxSize = 8
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


