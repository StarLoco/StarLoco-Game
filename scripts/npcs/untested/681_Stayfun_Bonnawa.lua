local npc = Npc(681, 120)

npc.colors = {16119285, 12809237, 15921906}
npc.accessories = {0, 915, 0, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2843)
    end
end

RegisterNPCDef(npc)
