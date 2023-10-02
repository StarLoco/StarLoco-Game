local npc = Npc(693, 30)

npc.colors = {2425089, 16777215, 16777215}
npc.accessories = {0, 700, 0, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2879)
    end
end

RegisterNPCDef(npc)
