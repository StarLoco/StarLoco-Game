local npc = Npc(571, 9073)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2330, {1936, 1937})
    elseif answer == 1936 then p:ask(2331, {1954})
    elseif answer == 1954 then p:ask(2349)
    elseif answer == 1937 then p:ask(2332, {4840})
    elseif answer == 4840 then p:endDialog()
    end
end

RegisterNPCDef(npc)
