local npc = Npc(111, 9015)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(370, {303, 302})
    elseif answer == 302 then p:ask(371)
    elseif answer == 303 then p:ask(372, {304})
    elseif answer == 304 then p:ask(374)
    end
end

RegisterNPCDef(npc)
