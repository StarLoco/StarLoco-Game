local npc = Npc(475, 1245)

npc.colors = {2955865, 8138547, 10197916}
npc.accessories = {0, 0, 2118, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2120)
    end
end

RegisterNPCDef(npc)
