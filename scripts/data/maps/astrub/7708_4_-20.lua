local map = MapDef(
	7708,
	"0905131019",
	"40533c6d672d27354a3820324b2f53603d512532425b654e3b652e36616d28766968432d7473645b7a74746d2d457c7d2f6457387e57503c222a4f565679212926217958345f3a7e642873773870754324524d6a366a74693870275d543d5e24326f4e58722972397357354f7c5f2138293f60427a7a2c3131224733682022434d7e53736a676a3f316575557c345b755154374174624d2f6b4447687e667b5f537d503664203b2966692e4c2a61514737725a616a535d4c4334326b434a3b5c4b5f6d5a435961684277392d62207b4b435c397e6d6f5e633d435f60684a402e553f7772214c49535a447739776c365362244671766d3d306546",
	"bhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaaHhaaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaaGhaaeE2aaaHhaaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaaGhaaeaaaaaGhaaeaaaaaHhaaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaaHhaaeE1aaaHhaaeaaahJGhaaeE0aaaHhaaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaaHhaaeE1aaaHhdReaaahYHhGaeaaaaaGhaaeE0aaaHhaaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaaHhaaeE1aaaHhGaeE3iaaHhGaeaaaaaHhGaenPaaaGhaaeE0aaaHhaaeaaaaabhGaeaaaaabhGaeaaaaaHhaaeE1aaaHhdReE4iaaHhGaeaaaaaHhdReaaahYHhGaeE4aaaGhaaeE0aaaHhaaeaaaaabhGaeaaaaaHhaaeE1aaaHhaaeE3ihNHhGaeaaaaaHhaaeaaaaaHhGaeaaaaaHhaaeE3agAGhaaeE0aaaHhaaeaaaaaHhaaeaaaaaHhdReaaaaaHhGaeaaaaaHhJReaaahSHhaaeaaaaaHhJReaaaaaHhqaeqgahFHhaaeaaaaabhGaeaaaaaHhaaeaaaaaHhGaeosaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaahYHhGaeaaaaaHhaaeaaaaabhGaeaaaaabhGaeaaaaaHhaaeaaaaaHhJReaaaaaHhGaeaaaaaHhJReaaaaaHhGaeaaaaaHhaaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaGhaaeaaaxOHhtQeqgaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaaHhaaeaaaaaGhdReaaahEHhaaeaaaaaHhaaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaaHhaaeaaaaaGhaaeaaahZHhaaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaaHhaaeaaaaaHhaaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaaHhaaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaa",
	9,
	12,
	4,
	-20,
	95
)

map.positions = "bIbQbRbYb0b8cfco|cccdckclcmctcucC"
map.capabilities = 2
map.mobGroupsCount = 1
map.mobGroupsMaxSize = 3

-- '0;0;0;0;0;0;0' forbiddens -> capabilities ? Or script ?

map.onMovementEnd = {
	[134] = moveEndTeleport(7710, 133),
	[159] = moveEndTeleport(7410, 231),
}

