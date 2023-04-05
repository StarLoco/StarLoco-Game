local npc = Npc(983, 1073)

local routes = {
    [11862] = {11866, 344},
    [11866] = {11862, 253},
}

function npc:onTalk(p, answer)
    if(answer == 0) then p:ask(4670, {4012})
    elseif(answer == 4012) then
        local dest = routes[p:mapID()]
        if not dest then return end
        p:sendAction(-1, 2, "6")
        p:teleport(dest[1], dest[2])
        p:endDialog()
    end
end

RegisterNPCDef(npc)
