local npc = Npc(645, 120)

npc.colors = {9603993, 16777215, 10925736}
npc.accessories = {0, 1019, 0, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2689, {3719, 2532})
    elseif answer == 2532 then p:ask(2895, {2533})
    elseif answer == 2533 then p:endDialog()
    elseif answer == 3719 then p:endDialog()
    end
end

RegisterNPCDef(npc)
