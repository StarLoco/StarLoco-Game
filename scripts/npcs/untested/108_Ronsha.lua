local npc = Npc(108, 9014)

npc.gender = 1

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(362, {296})
    elseif answer == 296 then p:ask(363)
    end
end

RegisterNPCDef(npc)