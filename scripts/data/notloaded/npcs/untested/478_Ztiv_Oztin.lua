local npc = Npc(478, 40)

npc.colors = {13473042, 14221312, 11842742}
npc.accessories = {0, 0, 932, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1979)
    end
end

RegisterNPCDef(npc)
