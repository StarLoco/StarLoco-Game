local map = MapDef(
	1559,
	"0712061508",
	"6a3c5c743c4d2e55315b445127607a6d783c5f396a57686a66514355744725323532525b697b7542452c29595a744a736d673053503a6a693b536d58512532425a385962374234527e5e604575483c6252504c7d4b5e29367b6f2f5020437b446c3e7c54253242247474664c404f7a2c5b5b3f477a752532424424275d5b3f647a5051384e6a6a2a692d5b6c2f26463d406d425b733a2a382150253242667830214b346e76253242585835433b473b757b52695b30",
	"bhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaaHhaaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaaHhaaejiaaaHhaaejjaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaaHhaaejiaaaHhbeeaaagwHhaaejjaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaaHhaaejiaaaHhaieaaagFHhGigaaaaaHhaaejjaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaaHhaaejiaaaHhaieaaaaaHhGAejkaaaHhGigaaaaaHhaaejjaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaaHhaaejiaaaHhaieaaanGHhGAeaaaaaHhGAeaaaaaHhGigaaaaaHhaaejjaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaaHhaaejiaaaHhGien_aaaHhaAeaaahgHhaAeaaehgHhGAeaaaaaHhGigaaahbHhaaejjaaabhGaeaaaaabhGaeaaaaabhGaeaaaaaHhaaejiaaaHhGieaaaaaHhGAeaaaaaHhaAeaaaaaHhGAeaaaaaHhGAeaaaaaHhGigaaaaaHhaaejjaaabhGaeaaaaabhGaeaaaaabhGaeaaaaaHhHeeaaaaaHhGAeqgaaaHhGAeaaehTHhaAeaaagxHhGAeaaaaaHhGAeaaaaaHhaign_iaaHhaaejjaaabhGaeaaaaabhGaeaaaaabhGaejnaaaGhaigaaahFHhGAeaaaaaHhaAeaaahhHhGAeaaaaaHhGAeaaag6HhaAeaaaaaHhaigaaaaaHhaaejjaaabhGaeaaaaabhGaeaaaaabhGaeaaaaaHhGigaaaaaHhGAeaaaaaHhGAeaaaaaHhGAeaaaaaHhaAeaaaaaHhaAeaaaaaHhaigaaag7HhaaejjaaabhGaeaaaaabhGaeaaaaabhGaeaaaaaHhGigaaaaaHhGAeaaaaaHhGAeaaaaaHhGAeaaaaaHhaAeaaaaaHhGAeaaaaaHhHeeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaaHhGigaaaaaHhGAeaaaaaHhGAeaaaaaHhGAeaaaaaHhGAeaaaaaHhGieaaaaabhGaejnaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaaHhGigaaaaaHhGAeaaaaaHhGAeaaaaaHhGAeaaaaaHhGieaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaaHhGigaaaaaHhGAeaaaaaHhGAeaaaaaHhGieaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaaHhHeeaaaaaHhGAeaaaaaHhaieaaagubhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaaHhGheqgaaaHhGigaaaaaHhGieaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaaHhbeeaaanGbhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaa",
	11,
	13,
	11,
	8,
	3
)

map.positions = "bzbUclcmc6c7dddy|bJbKcbcwcWdfdndo"
map.mobGroupsCount = 3
map.mobGroupsMinSize = 8
map.npcs = {
	[89] = {142, 1},
}
-- '0;0;0;0;0;0;0' forbiddens -> capabilities ? Or script ?

map.onMovementEnd = {
	[139] = moveEndTeleport(1560, 128),
	[225] = moveEndTeleport(670, 168),
}

RegisterMapDef(map)