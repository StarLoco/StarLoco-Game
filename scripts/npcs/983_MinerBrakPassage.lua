local npc = Npc(983, 1073)

local routes = {
    [11862] = {11866, 344},
    [11866] = {11862, 253},
}

function npc:onTalk(player, answer)
    if(answer == 0) then player:ask(4670, {4012})
    elseif(answer == 4012) then
        local dest = routes[player:mapID()]
        if not dest then return end
        player:sendAction(-1, 2, "6")
        player:teleport(dest[1], dest[2])
        player:endDialog()
    end
end

RegisterNPCDef(npc)
