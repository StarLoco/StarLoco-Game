local npc = Npc(336, 1197)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1334)
    end
end

RegisterNPCDef(npc)
