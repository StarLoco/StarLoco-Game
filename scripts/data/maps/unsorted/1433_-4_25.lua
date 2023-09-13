local map = MapDef(
	1433,
	"0706131721",
	"79355c414a62277b58215d4177743d5b337a2f41414440576b30565c2d60262f4174474b2a3d233c5e467e6775447a4f68646c20643742544720294b637d497c3e5d455e3c62344a3b2c2a5477637727473674512f476a505379644e7250267b44575e5f513b2923314d40527b72424f6e5f69553c6e624b566c3249484673755850552c714126433f4349265d206e5d776a705f487c595d6f6c41334b3c75516d4b385751765b3b21397c26387278454c7c5d294b637e",
	"bhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaaHhaaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaaHhaaej5iaaHhaaej5aaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaaHhaaej5iaaHhaAeaaaX_Hhaaej5aaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaaHhaaej5iaaHhaAebNaX7GhaAeaaexSHhaaej5aaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaaHhaaej5iaaHhaAeaaegBHhGAeaaaaaHhGAeaaaaaHhaaej5aaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaaHhaaej5iaaHhaAeaaaaaHhGAeaaaaaHhGAeaaaaaHhGAeaaaaaHhaaej5aaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaaHhaaej5iaaGhaAeaaexTHhGAeaaaaaGhaAeaaaxKHhGAeaaaaaHhaAeaaap_Hhaaej5aaabhGaeaaaaabhGaeaaaaabhGaeaaaaaHhaaej5iaaHhaAeaaan5HhGAeaaaaaHhaAem8aaaHhaAeaaahfHhGAeaaaaaHhaAeaaagZbhGaeaaaaabhGaeaaaaabhGaeaaaaaHhaaej5iaaHhGAeaaaaaHhGAeaaaaaHhaAeaaahfHhaAeaaaYcHhGAeaaaaaHhaAeaaanSbhGaeaaaaabhGaeaaaaabhGaeaaaaaHhaaej5iaaHhGAeaaaaaHhGAeaaaaaHhGAeaaaaaHhGAeaaaaaHhGAebNaaaHhaAeHtaX7bhGaeaaaaabhGaeaaaaabhGaeaaaaaHhaaej5iaaHhaAeaaaX8HhGAeaaaaaHhGAeaaaaaHhGAeaaaaaHhGAeaaaaaHhGAeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaaGhaAeaaexOHhGAeaaaaaHhGAeaaaaaHhGAeaaaaaHhGAeaaaaaHhGAeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaaHhaAeaaaaaHhGAeaaaaaHhGAeaaaaaHhGAeaaaaaHhGAeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaaHhGAeHtaaaHhGAebNaaaHhGAeaaaaaHhaAeaaaX8bhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaaHhGAeqgaaaHhGAeaaaaaHhGAeaaaaaHhGAeHtaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaaHhaAeaaaX6HhGAeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaaHhGAeHlaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaa",
	11,
	14,
	-4,
	25,
	315
)

map.positions = "c1dkdldQdRdSea|cRcScVc4dedmdy"
map.capabilities = 32
map.mobGroupsCount = 3
map.mobGroupsMinSize = 8
map.allowedMobGrades = {
	{88, 1},
	{88, 2},
	{88, 3},
	{88, 4},
	{88, 5},
}
-- '0;0;0;0;0;0;0' forbiddens -> capabilities ? Or script ?

map.onMovementEnd = {
	[233] = moveEndTeleport(1180, 446),
}

RegisterMapDef(map)