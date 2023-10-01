local npc = Npc(602, 9069)

npc.gender = 1

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2416)
    end
end

RegisterNPCDef(npc)
