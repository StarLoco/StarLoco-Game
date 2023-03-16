local npc = Npc(142, 9051)

npc.accessories = {}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(464)
    end
end

RegisterNPCDef(npc)